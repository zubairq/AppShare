function(args) {
/*
is_app(true)
control_type("VB")
display_name("REST API call control")
description("This will return the REST APIn call control")
base_component_id("rest_control")
load_once_from_file(true)
visibility("PRIVATE")
read_only(true)
properties(
    [
        {
            id:         "text",
            name:       "Dev text",
            default:    "REST API Call",
            type:       "String"
        }
        ,

        {
            id:         "has_details_ui",
            name:       "Has details UI?",
            type:       "Boolean",
            default:    true,
            hidden:     true
        }
        ,
        {
            id:         "width",
            name:       "Width",
            default:    180,
            type:       "Number"
        }
        ,
        {
            id:         "height",
            name:       "Height",
            default:    70,
            type:       "Number"
        }
        ,
        {
            id:             "callDefaultRestApi",
            pre_snippet:    `await `,
            snippet:        `callDefaultRestApi()`,
            name:           "Execute Command",
            type:           "Action"
        }
        ,
        {
            id:         "callRestApi",
            pre_snippet:    `await `,
            snippet:    `callRestApi("http://INSERT_URL_HERE")`,
            name:       "Execute Command",
            type:       "Action"
        }
        ,
        {
            id:         "URL",
            name:       "URL",
            default:    "https://raw.githubusercontent.com/typicode/demo/master/db.json",
            type:       "String"
        }


    ]
)//properties
logo_url("/driver_icons/rest.png")
*/


var allPaths = new Object()




function pathToString(pp) {
    var s = ""
    for (  var aa = 0  ;  aa < pp.length  ;  aa ++  ) {
        s += pp[aa]
        if (aa < pp.length -1) {
            s += "."
        }
    }
    return s
}





function addToPaths(path) {

    var cpath = pathToString(path)
    if (!allPaths[cpath]) {

        allPaths[cpath] = {
            count: 0,
            path: path
        }
    }

    allPaths[cpath].count ++
}




function isMap(o) {
    try {
        Map.prototype.has.call(o); // throws if o is not an object or has no [[MapData]]
        return true;
    } catch(e) {
        if (typeof o === 'object') {
            return true
        }
        return false;
    }
}



function findJsonPaths(currentPath,jsonNode) {
    addToPaths(currentPath)

    if (Array.isArray(jsonNode)) {
        //console.log("Found node: " )
        for (var k = 0 ; k < jsonNode.length ; k++) {

            //console.log("Key: " + k)
            var newPath = currentPath.concat(["[]"])
            findJsonPaths( newPath, jsonNode[k])
        }

    }  else if (isMap(jsonNode)) {
        var keys = Object.keys(jsonNode)
        //console.log("Found map:.. " + keys.length)
        for (var k = 0 ; k < keys.length ; k++) {

            //console.log("Key: " + keys[k])
            var newPath = currentPath.concat([keys[k]])

            findJsonPaths( newPath, jsonNode[keys[k]])
        }


    }  else if (typeof jsonNode === 'object') {
        //console.log("Found object: " )



    } else {
        //console.log("Found other: " + JSON.stringify(jsonNode,null,2))
    }
}



    Vue.component("rest_control",{

        props: ["meta", "args","design_mode","refresh"]

        ,



        template:

`<div v-bind:style='"height:100%;width:100%; border: 0px;" +
    "background-color: "+    args["background_color"]  +  ";"'>

    <div v-bind:style='"height:100%;width:100%; border: 0px;color:black;padding: 10px;"'
         v-if='design_mode == "detail_editor"'>
        {{args.text}}
        <div/>
        <button    class="btn btn-primary"
                   v-on:click="testDefaultRestApi()">

              Call API

        </button>

        <div/>
        <div style="height:100%;width:500px; border: 0px;color:black;padding: 10px;scroll:auto;">
            <pre>{{args.URL}}</pre>
            <div/>
            <pre>{{tempResult}}</pre>
        </div>
    </div>

    <div    v-else
            v-bind:style='"position:relative;width:100%;height:100%;border: 0px solid gray;background-color: "+    args["background_color"]  +  ";"'>

    <div    v-if='design_mode'>
           {{args.text}}
           <div/>
           <pre>{{args.URL}}</pre>
    </div>

        <div style="position:absolute;top:0px">
            <slot v-bind:refresh='refresh'>
            </slot>
        </div>
    </div>
</div>`

        ,

        data: function() {
            return {
                    tempResult: ""
            }
        }

        ,

        mounted: async function() {
            registerComponent(this)

        }
        ,


        methods: {
            callRestApiInternal: async function() {
                var mm = this

                var result = await callFunction(
                {
                    driver_name: "rest_call_service",
                    method_name: "rest_call_service"
                }
                ,
                {
                    URL:    mm.args.URL
                })


                if (result) {
                    return result
                }
                return null
            }
            ,


            callDefaultRestApi: async function() {

                var qwe = await this.callRestApiInternal()
                return qwe
            }
            ,


            callRestApi: async function(urlToCall) {

                var mm = this
                if (urlToCall) {
                    mm.args.URL = urlToCall
                }
                var qwe = await this.callRestApiInternal()
                return qwe
            }
            ,
            testDefaultRestApi: async function(urlToCall) {
                var rrr = await this.callDefaultRestApi()
                this.tempResult = rrr
                findJsonPaths(  [], rrr)
                alert(JSON.stringify(Object.keys(allPaths),null,2))
            }

        }




    })
}
