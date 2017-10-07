$.get("/", function(data){
    $( "#message" ).html( data.message );
});
