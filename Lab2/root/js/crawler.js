var template;

function updateStatus() {
    $.ajax({
        url: "/progress",
        dataType: "json",
        success: function(data) {
            $(".crawl-status").html(template({domains: data}));
            setTimeout(updateStatus, 1000);
        }
    })
}
$(function() {
    var source = $("#request-template").html();
    template = Handlebars.compile(source);
    updateStatus();
});

