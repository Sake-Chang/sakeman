'use strict';

$(function() {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");

    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    function getAjaxSettings(webMangaUpdateInfo) {
        return {
            type: "PUT",
            url: '/webLike',
            contentType: "application/json",
            data: JSON.stringify(webMangaUpdateInfo),
            dataType: 'json'
        };
    }

    $(document).on('click', '#web-manga-update-info-card-container .weblike-toggle', function() {
        let $this = $(this);
        let webMangaUpdateInfo = { id: $this.data('info-id') };

        $.ajax(getAjaxSettings(webMangaUpdateInfo))
            .done(function(data) {
                if (data.isLiked) {
                    $this.addClass('md-HL');
                } else {
                    $this.removeClass('md-HL');
                }
                $this.children('.btn-ia-text').text(data.weblikecount + " いいね");
            })
            .fail(function(jqXHR, testStatus, errorThrown) {
                alert('いいねできませんでした…');
            });
    });
});



//
//$(function(){
//
//    let token = $("meta[name='_csrf']").attr("content");
//    let header = $("meta[name='_csrf_header']").attr("content");
//    $(document).ajaxSend(function(e, xhr, options) {
//        xhr.setRequestHeader(header, token);
//    });
//
//    // 値を取得
//    let weblike = $('.weblike-toggle');
//    weblike.on('click', function(){
//        let $this = $(this);
//        let webMangaUpdateInfo = {id:$this.data('info-id')};
//        console.log(webMangaUpdateInfo);
//            $.ajax({
//                type            : "PUT",
//                url             : '/webLike',
//                contentType     : "application/json",
//                data            : JSON.stringify(webMangaUpdateInfo),
//                dataType        : 'json',
//            }).done(function(data){
//                $this.toggleClass('md-HL');
//                console.log(data);
//                $this.children('.btn-ia-text').text(data + " いいね");
//            }).fail(function(jqXHR,testStatus,errorThrown){
//                alert('いいねできませんでした…');
//            }).always(function(){
//        });
//    });
//})
