$(document).ready(function() {
    $("input[name='rating']").click(function() {
        let val = $("input[name='rating']:checked").val();
        console.log(val);
        $("span").removeClass("md-star");
        $("input[name='rating']:checked").next('label').children('span').toggleClass("md-star");
        $("input[name='rating']:checked").prevAll('label').children('span').toggleClass("md-star");
    });

    $('.select2').change(function(){
        let val2 = $('option:selected').val();
        console.log(val2);
        if (val2 != null) {
            $('.form-button').prop('disabled', false);
        }else{
            $('.form-button').setAttribute("disabled", true);
        }


    })
  });