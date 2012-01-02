Henry.site(function(context) {
    Henry.log("The site title is: " + site.title);
    Henry.log("Setting title to something else");
    context.site.title = "Something else";
});

Henry.post(function(context) {

});

Henry.log("Plugin loaded");

var something = {
    "test": "42"
};
