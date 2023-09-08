'use strict'

$(function(){

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    // 値を取得
    let like = $('.like-toggle');
    like.on('click', function(){
        let $this = $(this);
        let review = {id:$this.data('review-id')};
        console.log(review);
            $.ajax({
                type            : "PUT",
                url             : '/like',
                contentType     : "application/json",
                data            : JSON.stringify(review),
                dataType        : 'json',
            }).done(function(data){
                $this.toggleClass('md-HL');
                console.log(data);
                $this.children('.btn-ia-text').text(data + " いいね");
            }).fail(function(jqXHR,testStatus,errorThrown){
                alert('いいねできませんでした…');
            }).always(function(){
        });
    });
})
