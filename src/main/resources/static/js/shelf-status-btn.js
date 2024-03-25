'use strict'

$(function(){
    let token = $("meta[name='_csrf']").attr("content");
    let header = $("meta[name='_csrf_header']").attr("content");
    $(document).ajaxSend(function(e, xhr, options) {
        xhr.setRequestHeader(header, token);
    });

    // メニューを開閉
    let statusbtn = $('.dropdown__btn');
    statusbtn.on('click', function(){
        let $this = $(this);
        $this.toggleClass('is-open');
    });

    // メニューを開閉
    let statusbtn_mini = $('.dropdown__btn-mini');
    statusbtn_mini.on('click', function(){
        let $this = $(this);
        $this.toggleClass('is-open');
    });

    // ステータスを変更
    let item = $('.dropdown__item');
    item.on('click', function(){
        console.log("click");
        let $this2 = $(this);
        let status = $this2.text();
        let readStatus = {
            "status"    : $this2.text(),
            "id"     : $this2.data('id')
        };
        console.log(readStatus);
            $.ajax({
                type            : "PUT",
                url             : '/readstatus2',
                contentType     : "application/json",
                data            : JSON.stringify(readStatus),
                dataType        : 'json',
            }).done(function(data){
                console.log("成功");

                switch(status){
                    case "未登録"  :   $('.is-open').find('.status-icon').text('bookmark_add');
                                      $('.is-open').find('.status-icon').removeClass('md-WH');
                                      $('.is-open').find('.status-text').text('気になる');
                                      $('.is-open').find('.status-text').removeClass('md-WH');
                                      $('.is-open').removeClass('btn-highlight');
                                      $('.is-open').removeClass('btn-purple');
                                      break;
                    case "気になる" :  $('.is-open').find('.status-icon').text('bookmark');
                                      $('.is-open').find('.status-icon').addClass('md-WH');
                                      $('.is-open').find('.status-text').text('気になる');
                                      $('.is-open').find('.status-text').addClass('md-WH');
                                      $('.is-open').addClass('btn-highlight');
                                      $('.is-open').removeClass('btn-purple');
                                      break;
                    case "読んだ"  :   $('.is-open').find('.status-icon').text('check_box');
                                      $('.is-open').find('.status-icon').addClass('md-WH');
                                      $('.is-open').find('.status-text').text('読んだ！');
                                      $('.is-open').find('.status-text').addClass('md-WH');
                                      $('.is-open').removeClass('btn-highlight');
                                      $('.is-open').addClass('btn-purple');
                                      break;
                }

                $('.is-open').removeClass('is-open');
            }).fail(function(jqXHR,testStatus,errorThrown){
                alert('できませんでした…');
            }).always(function(){
        });
    });

})