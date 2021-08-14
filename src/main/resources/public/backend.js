const BLANK_PREFIX = " - "
const BACKEND_URL = "http://localhost:8081/nodes/"

let nodesInCache = {};
let nodesInDb = {}
let autoIncrementId = 0;

$(document).ready(function() {

    $.ajaxSetup({
      contentType: "application/json; charset=utf-8"
    });

    $( ".notice" ).fadeOut();

    loadAndRenderDbSelect();

    $("#load-selected-node").click(function(e) {
        let nodeIds = $('#db-select').val();
        if (nodeIds === undefined) {
            showMsg("Please select node in DB")
            return;
        }

        $.each(nodeIds, (i, nodeId)=> {
             $.get(BACKEND_URL + "get/" + nodeId).then(
                function(node) {
                    addToCache(node);
                    renderCacheSelect();
                }
             );
        });

    });

     $("#remove-node").click(function(e) {
         let $cache = $('#cache-select');
         let nodeIds = $cache.val();
         if (nodeIds === undefined) {
            showMsg("Please select node in cache")
            return;
         }

         $.each(nodeIds, (i, nodeId)=> {
             nodesInCache[nodeId].status = 'REMOVED';
         });

         $cache.find('option:selected').css('color', 'gray').prop('selected',false);
     });

     $("#add-node").click(function(e) {
            let nodeId = $('#cache-select').val()[0];
            if (nodeId === undefined) {
                showMsg("Please select node in cache")
                return;
            }

            let nodeName = prompt("Please enter node name", "Node");
            if (nodeName != null) {
                let newNode = {};
                let newId = "T" + autoIncrementId;
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
            let nodeId = $('#cache-select').val()[0];
            if (nodeId === undefined) {
                showMsg("Please select root node in cache")
                return;
            }

            let selectedNode = nodesInCache[nodeId];

            if (nodesInCache[nodeId].status === 'REMOVED') {
                showMsg("Editing removed node is denied")
                return;
            }

            let nodeName = prompt("Please enter new node name", selectedNode.value);
            if (nodeName != null) {
                selectedNode.value = nodeName;
                selectedNode.status = 'CHANGED';
                renderCacheSelect();
            }
     });

     $("#save").click(function(e) {
            let nodesArray = $.map(nodesInCache, function(value, index){
                    return [value];
            });

            $.post( BACKEND_URL + "save", JSON.stringify(nodesArray))
                .done(function( data ) {
                    loadAndRenderDbSelect();
                    $('#cache-select').empty();
                    nodesInCache = {};
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
    let sortedNodeIds = sortNodeIds(nodesInCache)
    displayNodes($('#cache-select'), nodesInCache, sortedNodeIds)
}

function renderDbSelect() {
    let sortedNodeIds = sortNodeIds(nodesInDb);
    displayNodes($('#db-select'), nodesInDb, sortedNodeIds)
}

function sortNodeIds(nodes) {
    let nodesArray = $.map(nodes, function(value, index){
            return [value];
    });

    if (nodesArray.length === 0) {
        return;
    }

    let parentIds = nodesArray.map(o => o.parentId)
    let ids = nodesArray.map(o => o.id)
    let leafs = ids.diff(parentIds) //get leafs of tree

    //build paths from all leafs to root
    let pathsFromLeafToRoot = [];
    for (const leafId of leafs){
        let currentTree = [];
        buildPathRecursively(nodes, nodes[leafId], currentTree);
        pathsFromLeafToRoot.push(currentTree);
    }

    calculateLevelsOfNodes(pathsFromLeafToRoot, nodes);

    //build global order of node ids
    let orderedNodeIds = Array.from(pathsFromLeafToRoot[0]);
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
    if (node.parentId === undefined || nodes[node.parentId] === undefined) {
        builtPath.reverse();
        return;
    } else {
        buildPathRecursively(nodes, nodes[node.parentId], builtPath)
    }
}

function addChainToNodeIdsArray(sortedTree, branch) {
    let lastIndex = -1;
    let count = 0;
    for (const branchId of branch){
        let index = sortedTree.indexOf(branchId);
        if (index >= 0) {
            lastIndex = index;
            count++;
        } else {
            break
        }
    }

    let arrForInsert = branch.slice(count, branch.length);
    sortedTree.insert(lastIndex + 1, arrForInsert);
}

function displayNodes(el, nodes, orderedNodeIds) {
    el.empty();
    $.each(orderedNodeIds, function( i, id ) {
        let o = nodes[id]
        let option = new Option(BLANK_PREFIX.repeat(o.level) + o.value, o.id);
        if (o.status === 'REMOVED') {
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

