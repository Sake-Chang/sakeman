'use strict'

$(function(){

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    // 値を取得
    let follow = $('.web-manga-follow-toggle');
    follow.on('click', function(){
        let $this = $(this);
        let mangaId = {id:$this.data('manga-id')}; //変えたmangaId
        let thisData = $this.data('manga-id');
        console.log(mangaId);//mangaId
            $.ajax({
                type            : "PUT",
                url             : '/api/webmangafollow',
                contentType     : "application/json",
                data            : JSON.stringify(mangaId),
                dataType        : 'json',
            }).done(function(data){
                let listItem = $('div.web-manga-follow-toggle');
                let obj = $(listItem.filter(function(){
                    return($(this).data('manga-id') === thisData);
                    }))

                obj.children().toggleClass('md-HL');
                if ($this.children('.material-icons-outlined').text() === 'bookmark') {
                    obj.children('.material-icons-outlined').text('bookmark_add');
                } else {
                    console.log('エルス');
                    obj.children('.material-icons-outlined').text('bookmark');
                }
                console.log('発火');
            }).fail(function(jqXHR,testStatus,errorThrown){
                alert('フォローできませんでした…');
            }).always(function(){
        });
    });
})
