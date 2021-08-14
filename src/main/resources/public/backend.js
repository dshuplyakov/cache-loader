const BLANK_PREFIX = " - "
const BACKEND_URL = "http://localhost:8081/nodes/"

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
        if (nodeId == undefined) {
            showMsg("Please select node in DB")
            return;
        }

        $.get(BACKEND_URL + "get/" + nodeId).then(
            function(node) {
                addToCache(node);
                renderCacheSelect();
            }
        );
    });

     $("#remove-node").click(function(e) {
            var nodeId = $('#cache-select').val()[0]; 
            if (nodeId == undefined) {
                showMsg("Please select node in cache")
                return;
            }

            nodesInCache[nodeId].status = 'REMOVED';
            $('#cache-select').find('option:selected').css('color', 'gray').prop('selected',false);
     });

     $("#add-node").click(function(e) {
            var nodeId = $('#cache-select').val()[0];
            if (nodeId == undefined) {
                showMsg("Please select node in cache")
                return;
            }

            var nodeName = prompt("Please enter node name", "Node");
            if (nodeName != null) {
                var newNode = new Object();
                var newId = "T" + autoIncrementId;
                newNode.id = newId;
                newNode.parentId = nodesInCache[nodeId].id;
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
                showMsg("Please select root node in cache")
                return;
            }

            var selectedNode = nodesInCache[nodeId];

            if (nodesInCache[nodeId].status == 'REMOVED') {
                showMsg("Editing removed node is denied")
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
            $.post( BACKEND_URL + "save", nodesArray)
                .done(function( data ) {
                    loadAndRenderDbSelect();
            });
     });

     $("#reset").click(function(e) {
            $.get(BACKEND_URL + "reset")
                .done(function( data ) {
                    loadAndRenderDbSelect();
                    nodesInCache = {};
                    renderCacheSelect();
            });
     });

});

function loadAndRenderDbSelect() {
    $.ajax({
        url: BACKEND_URL + "load"
    }).then(function(data) {
           nodesInDb = {};
           $.each(data, (i, o)=> {
                nodesInDb[o.id] = o;
             });
           renderDbSelect();
    });
}

function renderCacheSelect() {
    var sortedNodeIds = sortNodeIds(nodesInCache)
    displayNodes($('#cache-select'), nodesInCache, sortedNodeIds)
}

function renderDbSelect() {
    var sortedNodeIds = sortNodeIds(nodesInDb);
    displayNodes($('#db-select'), nodesInDb, sortedNodeIds)
}

function sortNodeIds(nodes) {
    var nodesArray = $.map(nodes, function(value, index){
            return [value];
    });

    if (nodesArray.length == 0) {
        return;
    }

    var parentIds = nodesArray.map(o => o.parentId)
    var ids = nodesArray.map(o => o.id)
    var leafs = ids.diff(parentIds) //get leafs of tree

    //build paths from all leafs to root
    var pathsFromLeafToRoot = [];
    for (const leafId of leafs){
        var currentTree = [];
        buildPathRecursively(nodes, nodes[leafId], currentTree);
        pathsFromLeafToRoot.push(currentTree);
    }

    calculateLevelsOfNodes(pathsFromLeafToRoot, nodes);

    //build global order of node ids
    var orderedNodeIds = Array.from(pathsFromLeafToRoot[0]);
    $.each(pathsFromLeafToRoot, function( i, arr ) {
        if (i>0) {
            addChainToNodeIdsArray(orderedNodeIds, arr);
        }
    });

    return orderedNodeIds
}

function calculateLevelsOfNodes(pathsArr, nodes){
     $.each(pathsArr, function( i, arr ) {
        $.each(arr, function( i, el ) {
            nodes[el].level = i;
        });
     });
}

// build path from leaf to root
function buildPathRecursively(nodes, node, builtPath) {
    builtPath.push(node.id);
    if (node.parentId == undefined || nodes[node.parentId] == undefined) {
        builtPath.reverse();
        return;
    } else {
        buildPathRecursively(nodes, nodes[node.parentId], builtPath)
    }
}

function addChainToNodeIdsArray(sortedTree, branch) {
    var lastIndex = -1;
    var count = 0;
    for (const branchId of branch){
        var index = sortedTree.indexOf(branchId);
        if (index >= 0) {
            lastIndex = index;
            count++;
        } else {
            break
        }
    }

    var arrForInsert = branch.slice(count, branch.length);
    sortedTree.insert(lastIndex + 1, arrForInsert);
}

function displayNodes(el, nodes, orderedNodeIds) {
    el.empty();
    $.each(orderedNodeIds, function( i, id ) {
        var o = nodes[id]
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

function showMsg(msg) {
  $( "#tooltip" ).html('<div class="notice info">'+msg+'</p></div>').fadeIn().delay(2000).fadeOut();
};


//UTILS
Array.prototype.diff = function(arr2) { return this.filter(x => !arr2.includes(x)); }
Array.prototype.insert = function (index, items) {     this.splice.apply(this, [index, 0].concat(items)); }

