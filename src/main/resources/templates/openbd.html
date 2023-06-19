<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">

    <title>書誌情報取得サンプル</title>

    <!-- Scripts -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js"></script>
</head>
<body>

    <div>
        ISBN13：<input id="isbn" type="text" name="isbn" value="" autofocus>
        <button id="getBookInfo" class="btn btn-default">書籍情報取得</button>
    </div>

    <div>
        <p id="thumbnail"></p>
    </div>

    <div>
        書籍名：<input id="title" type="text" name="title" value="">
    </div>

    <div>
        出版社：<input id="publisher" type="text" name="publisher" value="" >
    </div>

    <div>
        巻：<input id="volume" type="text" name="volume" value="" >
    </div>

    <div>
        シリーズ：<input id="series" type="text" name="series" value="" >
    </div>

    <div>
        著者：<input id="author" type="text" name="author" value="" >
    </div>

    <div>
        出版日：<input id="pubdate" type="text"  name="pubdate" value="" >
    </div>

    <div>
        サムネイルURI：<input id="cover" type="text" name="cover" value="" >
    </div>

    <div>
        詳細：<textarea id="description" type="text" name="description" value="" ></textarea>
    </div>

    <script>
        $(function() {
            $('#getBookInfo').click( function( e ) {
                e.preventDefault();
                const isbn = $("#isbn").val();
                const url = "https://api.openbd.jp/v1/get?isbn=" + isbn;

                $.getJSON( url, function( data ) {
                    if( data[0] == null ) {
                        alert("データが見つかりません");
                    } else {
                        if( data[0].summary.cover == "" ){
                            $("#thumbnail").html('<img src=\"\" />');
                        } else {
                            $("#thumbnail").html('<img src=\"' + data[0].summary.cover + '\" style=\"border:solid 1px #000000\" />');
                        }
                        $("#title").val(data[0].summary.title);
                        $("#publisher").val(data[0].summary.publisher);
                        $("#volume").val(data[0].summary.volume);
                        $("#series").val(data[0].summary.series);
                        $("#author").val(data[0].summary.author);
                        $("#pubdate").val(data[0].summary.pubdate);
                        $("#cover").val(data[0].summary.cover);
                        $("#description").val(data[0].onix.CollateralDetail.TextContent[0].Text);
                    }
                });
            });
        });
    </script>

</body>
</html>