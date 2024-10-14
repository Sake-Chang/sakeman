// ボタンのHTMLを生成する関数
function renderActionButtons(data, updateUrl, deleteUrl) {
    return `
        <td class="td-center">
            <div class="admin-btn-container">
                <a href="${updateUrl}/${data}" class="btn btn-navy">
                    <i class="bi bi-pencil-square"></i>
                </a>
                <a href="${deleteUrl}/${data}" class="btn btn-danger" onclick="return confirm('ほんまに削除して良いん？');">
                    <i class="bi bi-trash-fill"></i>
                </a>
            </div>
        </td>
    `;
}

function initializeDataTable(tableSelector, ajaxUrl, columns, registerUrl, updateUrl, deleteUrl) {
    $(tableSelector).DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": {
            "url": ajaxUrl,
            "type": "GET"
        },
        "columns": columns.concat([{
            "data": "id",
            "orderable": false,
            "render": function(data, type, row, meta) {
                return renderActionButtons(data, updateUrl, deleteUrl);
            }
        }]),
        dom: "<'row centered'<'col-2'B>>" +
             "<'row centered'<'col-6'l><'col-6'f>>" +
             "<'row centered'<'col-12'p>>" +
             "<'row'<'col-12'tr>>" +
             "<'row centered'<'col-12'p>>",
        buttons: [
            {
                text: '+',
                action: function ( e, dt, node, config ) {
                    window.location.href = registerUrl;
                },
                className: 'btn btn-primary'
            }
        ],
        language: {
            url: "/webjars/datatables-plugins/i18n/ja.json",
            "buttons": {"csv": "CSV"}
        },
        paging: true,
        ordering: true,
        searching: true,
        info: true,
        responsive: true,
        pageLength: 10,
        lengthMenu: [10, 25, 50, 100]
    });
}

// Mangaのテーブル
initializeDataTable('.admin-table-manga', '/admin/manga/api', [
    { "data": "id" },
    { "data": "title" },
    { "data": "authors", "orderable": false },
    { "data": "volume" },
    { "data": "publisher" },
    { "data": "publishedIn" }
], '/admin/manga/register', '/admin/manga/update', '/admin/manga/delete');

// WebMangaUpdateInfoのテーブル
initializeDataTable('.admin-table-webmangaupdateinfo', '/admin/web-manga-update-info/api', [
    { "data": "id" },
    { "data": "mediaName" },
    { "data": "webMangaMedia" },
    { "data": "titleString" },
    { "data": "manga" },
    { "data": "subTitle" },
    { "data": "authorString" },
], '/admin/web-manga-update-info/register', '/admin/web-manga-update-info/update', '/admin/web-manga-update-info/delete');
