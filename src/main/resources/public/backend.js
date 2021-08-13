const BLANK_PREFIX = " - "

var nodesInCache = {};
var nodesInDb = {}
var autoIncrementId = 0;

$(document).ready(function() {

    $.ajaxSetup({
      contentType: "application/json; charset=utf-8"
    });

    $( ".notice" ).fadeOut();

    loadAndRenderDbSelect();

    $("#load-selected-node").click(function(e) {
        var nodeId = $('#db-select').val()[0]; 
        $.get("http://localhost:8081/database/nodes/get/" + nodeId).then(
          function(node) {
            addToCache(node);
            renderCacheSelect();
          }
        );
    });

     $("#remove-node").click(function(e) {
            var nodeId = $('#cache-select').val()[0]; 
            if (nodeId == undefined) {
                alert("Please select node in cache")
                return;
            }

            nodesInCache[nodeId].status = 'REMOVED';
            $('#cache-select').find('option:selected').css('color', 'gray').prop('selected',false);
     });

     $("#add-node").click(function(e) {
            var nodeId = $('#cache-select').val()[0]; 
            if (nodeId == undefined) {
                alert("Please select node in cache")
                return;
            }

            var nodeName = prompt("Please enter node name", "Node");
            if (nodeName != null) {
                var selectedNodeLevel = nodesInCache[nodeId].level;
                var newNode = new Object();
                var newId = "T" + autoIncrementId;
                newNode.id = newId;
                newNode.level = selectedNodeLevel + 1;
                newNode.value = nodeName;
                newNode.status = 'NEW';
                nodesInCache[newId] = newNode;
                autoIncrementId++;
                renderCacheSelect();
            }
     });

     $("#edit-node").click(function(e) {
            var nodeId = $('#cache-select').val()[0]; 
            if (nodeId == undefined) {
                alert("Please select root node in cache")
                return;
            }

            var selectedNode = nodesInCache[nodeId];

            if (nodesInCache[nodeId].status == 'REMOVED') {
                alert("Editing removed node is denied")
                return;
            }

            var nodeName = prompt("Please enter new node name", selectedNode.value);
            if (nodeName != null) {
                selectedNode.value = nodeName;
                selectedNode.status = 'CHANGED';
                renderCacheSelect();
            }
     });

     $("#save").click(function(e) {
            var nodesArray = $.map(nodesInCache, function(value, index){
                    return [value];
            });

            nodesArray = JSON.stringify(nodesArray);
            $.post( "http://localhost:8081/database/nodes/save", nodesArray)
                .done(function( data ) {
                    loadAndRenderDbSelect();
            });
     });

     $("#reset").click(function(e) {
            $.get("http://localhost:8081/database/nodes/reset")
                .done(function( data ) {
                    loadAndRenderDbSelect();
                    nodesInCache = {};
                    renderCacheSelect();
            });
     });

});

function loadAndRenderDbSelect() {
    $.ajax({
        url: "http://localhost:8081/database/nodes/load"
    }).then(function(data) {
           nodesInDb = {};
           $.each(data, (i, o)=> {
                nodesInDb[o.id] = o;
             });
           renderDbSelect();
    });
}

function renderCacheSelect() {
    displayNodes($('#cache-select'), nodesInCache)
}


function renderDbSelect() {
    displayNodes($('#db-select'), nodesInDb)
}


function displayNodes(el, data) {
    var sortedNodes = sortNodes(data)
    el.empty();
    $.each(sortedNodes, function( i, o ) {
        var option = new Option(BLANK_PREFIX.repeat(o.level) + o.value, o.id);
        if (o.status == 'REMOVED') {
            option.style="color: gray;";
        }
        el.append(option);
    });
}

function addToCache(node) {
    nodesInCache[node.id] = node
}


function sortNodes(nodesObject) {
    var sorted = $.map(nodesObject, function(value, index){
            return [value];
    });
    sorted.sort(function(a,b) {
          if ( a.level < b.level ){
            return -1;
          }
          if ( a.level > b.level ){
            return 1;
          }
          if (a.level = b.level) {
              var x = a.value.toLowerCase();
              var y = b.value.toLowerCase();
              return x < y ? -1 : x > y ? 1 : 0;
          }
          return 0;
    });
    return sorted;
}

