'use strict';

$(function() {
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    function getAjaxSettings(mangaId) {
        return {
            type: "PUT",
            url: '/api/webmangafollow',
            contentType: "application/json",
            data: JSON.stringify({ id: mangaId }),
            dataType: 'json'
        };
    }

    $(document).on('click', '.web-manga-follow-toggle', function() {
        let $this = $(this);
        let mangaId = $this.data('manga-id');
        if (mangaId == null) {
            alert('Manga IDが見つかりません。');
            return;
        }

        $.ajax(getAjaxSettings(mangaId))
            .done(function(data) {
                let icon = $this.find('.material-icons-outlined');
                icon.text(data.isFollowing ? 'bookmark' : 'bookmark_add').toggleClass('md-HL', data.isFollowing);
            })
            .fail(function(jqXHR, testStatus, errorThrown) {
                alert('フォローできませんでした…');
            });
    });
});