//TODO give all of the context to each plugin and execute, it gets to communicate using the context
//TODO maybe add some way of defining callback Henry.addListener("event", callback)
Henry.site(function(context) {
    console.log("The site title is: " + site.title);
    console.log("Setting title to something else");
    context.site.title = "Something else";
});

Henry.post(function(context) {

});
