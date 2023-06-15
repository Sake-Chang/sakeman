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
        let manga = {id:$this.data('manga-id')};
        let thisData = $this.data('manga-id');
        console.log(manga);
            $.ajax({
                type            : "PUT",
                url             : '/readstatus',
                contentType     : "application/json",
                data            : JSON.stringify(manga),
                dataType        : 'json',
            }).done(function(data){

                let listItemMini = $('div.status-toggle-mini');
                let listItem = $('div.status-toggle');
                let objMini = $(listItemMini.filter(function(){
                            return($(this).data('manga-id') === thisData);
                        }))
                let obj = $(listItem.filter(function(){
                            return($(this).data('manga-id') === thisData);
                        }))
                console.log(objMini);
                console.log(obj);

                objMini.children('span').toggleClass('md-ffffff');
                objMini.toggleClass('btn-highlight');
                if ($this.find('.status-icon').text() === 'bookmark') {
                    objMini.find('.status-icon').text('bookmark_add');
                } else {
                    objMini.find('.status-icon').text('bookmark');
                }

                obj.toggleClass('md-highlight');
                if (obj.find('.status-icon').text() === 'bookmark') {
                    obj.find('.status-icon').text('bookmark_add');
                } else {
                    obj.find('.status-icon').text('bookmark');
                }
            }).fail(function(jqXHR,testStatus,errorThrown){
                alert('気になるできませんでした…');
            }).always(function(){
        });
    });

    let status2 = $('.status-toggle-mini');
    status2.on('click', function(){
        let $this = $(this);
        let manga = {id:$this.data('manga-id')};
        let thisData = $this.data('manga-id');
        console.log(manga);
            $.ajax({
                type            : "PUT",
                url             : '/readstatus',
                contentType     : "application/json",
                data            : JSON.stringify(manga),
                dataType        : 'json',
            }).done(function(data){
                let listItemMini = $('div.status-toggle-mini');
                let listItem = $('div.status-toggle');
                let objMini = $(listItemMini.filter(function(){
                            return($(this).data('manga-id') === thisData);
                        }))
                let obj = $(listItem.filter(function(){
                            return($(this).data('manga-id') === thisData);
                        }))
                console.log(objMini);
                console.log(obj);

                objMini.children('span').toggleClass('md-ffffff');
                objMini.toggleClass('btn-highlight');
                if ($this.find('.status-icon').text() === 'bookmark') {
                    objMini.find('.status-icon').text('bookmark_add');
                } else {
                    objMini.find('.status-icon').text('bookmark');
                }

                obj.toggleClass('md-highlight');
                if (obj.find('.status-icon').text() === 'bookmark') {
                    obj.find('.status-icon').text('bookmark_add');
                } else {
                    obj.find('.status-icon').text('bookmark');
                }

            }).fail(function(jqXHR,testStatus,errorThrown){
                alert('気になるできませんでした…');
            }).always(function(){
        });
    });
})
