'use strict'

$(function(){

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    // 値を取得
    let status = $('.status-toggle');
    status.on('click', function(){
        let $this = $(this);
        let mangaId = {id:$this.data('manga-id')};
//        console.log(manga);
            $.ajax({
                type            : "PUT",
                url             : '/readstatus',
                contentType     : "application/json",
                data            : JSON.stringify(mangaId),
                dataType        : 'json',
            }).done(function(data){
                let $these = document.querySelectorAll('*[manga-id == mangaId]');
                $these.toggleClass('md-HL');
                if ($these.find('.status-icon').text() === 'bookmark') {
                    $these.find('.status-icon').text('bookmark_add');
                } else {
                    $these.find('.status-icon').text('bookmark');
                }
            }).fail(function(jqXHR,testStatus,errorThrown){
                alert('気になるできませんでした…');
            }).always(function(){
        });
    });
})
