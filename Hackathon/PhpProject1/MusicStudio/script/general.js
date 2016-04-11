$(document).ready(function(){
   $(".tab, .tab_pressed").on("click",function(){show_tab(this);});
});

function show_tab(tab){
    $(".tab_pressed").attr("class","tab");
    $(".tab_detail").attr("class","tab_detail_hidden");
    $(tab).attr("class","tab_pressed");
    $(tab).children(".tab_detail_hidden").attr("class","tab_detail");
}

function htmlMaker(template) { //this class is used to generate dynamic HTML
    this.template = template;
}

htmlMaker.prototype.setTemplate=function(template){
    this.template=template;
}

//the parameters are passed as an associative array. They are used as parameters in the template
htmlMaker.prototype.getHTML = function (values) {
    var html = "";
    if ($.isArray(values))
        for (var i = 0; i < values.length; ++i) {
            html+=this.get_html_unrepeated(values[i]);
        }
    else
        html=this.get_html_unrepeated(values);

    return html;
};


htmlMaker.prototype.get_html_unrepeated=function(values){
    var html=this.template;
            for (var key in values){
                if ($.isArray(values[key])) 
                    html= this.replace_Block(key, values[key],html);
                else{ 
                    var re = new RegExp("{{" + key + "}}", 'g');
                    html = html.replace(re,values[key]);
                }
            }
            return html;
}

//the parameters are used for a repetitive a block
htmlMaker.prototype.replace_Block = function (block_name, values,template) {
    var block_start_text = "{{#block " + block_name + "}}";
    var block_end_text = "{{#end block " + block_name + "}}";
    var start_index = template.search(block_start_text) + block_start_text.length;
    var end_index = template.search(block_end_text);
    var block = template.substring(start_index, end_index);
    template = template.substring(0, start_index) + template.substring(end_index, template.length - 1);
    if (block.length > 0) {
        var blocks = "";
        var html_maker=new htmlMaker(block);
        blocks=html_maker.getHTML(values);
        template= template.replace(block_start_text, blocks);
        template = template.replace(block_end_text, "");
        }
    return template;
};


