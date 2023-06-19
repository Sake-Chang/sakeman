'use strict'

$(function(){

    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    // 値を取得
    let follow = $('.userfollow-toggle');
    follow.on('click', function(){
        let $this = $(this);
        let objUser = {id:$this.data('followee-user-id')};
        console.log(objUser);
            $.ajax({
                type            : "PUT",
                url             : '/sakeman/follow',
                contentType     : "application/json",
                data            : JSON.stringify(objUser),
                dataType        : 'json',
            }).done(function(data){
                $this.children().toggleClass('md-highlight');
                if ($this.children('.material-icons-outlined').text() === 'person_outline') {
                    $this.children('.material-icons-outlined').text('person_add_alt');
                    $this.children('.btn-user-follow-userpage-text').text('フォローする');
                } else {
                    $this.children('.material-icons-outlined').text('person_outline');
                    $this.children('.btn-user-follow-userpage-text').text('フォロー中');
                }
                console.log('発火');
            }).fail(function(jqXHR,testStatus,errorThrown){
                alert('フォローできませんでした…');
            }).always(function(){
        });
    });
})
