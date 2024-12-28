<html>
<head>
    <style type="text/css">
        *{font-family: '微软雅黑';font-size: 14px;padding: 0;margin: 0;}
        table {
            border-collapse: collapse;
            border-spacing: 0;
            font-family: '微软雅黑';
            font-size: 12px;
            display: table;
            border-color: grey;
        }
        }
        .table {
            white-space: pre-wrap;
            table-layout: fixed;
            width: 100%;
            margin-bottom: 20px;
        }
        .table>thead>tr>th {
            padding: 6px;
            font-size: 14px;
            font-weight: 600;
            display: table-cell;
        }

        .table>tbody>tr:last-of-type {
            border-bottom: 1px solid rgb(193, 199, 208);
        }
        tr {
            display: table-row;
            vertical-align: inherit;
            border-color: inherit;
        }
        .table>thead:first-child>tr:first-child>th {
            border-color: rgb(193, 199, 208);
            border-bottom: 2px solid rgb(23, 43, 77);
            border-top: 1px solid rgb(193, 199, 208);
            background-color: rgb(244, 245, 247);
        }
        .table td, .table th {
            word-break: break-all !important;
            word-wrap: break-word !important;
            white-space: pre-wrap;
            font-size: 12px;
        }
        .table>tbody>tr>td, .table>tbody>tr>th, .table>tfoot>tr>td, .table>tfoot>tr>th, .table>thead>tr>td, .table>thead>tr>th {
            padding: 6px;
            line-height: 1.42857;
            vertical-align: middle;
            border-top: 1px solid rgb(193, 199, 208);
            border-left: 1px solid rgb(193, 199, 208) !important;
            border-right: 1px solid rgb(193, 199, 208) !important;
        }
        .text-center {
            text-align: center;
        }
        .xh{
            width: 80px !important;
        }
        body {
            color: rgb(23, 43, 77);
            line-height: 1.42857;
        }
    </style>
</head>
<body>
<div style="color: rgb(23, 43, 77); line-height: 22px;padding: 0 15px;">
    <!--数据填充-->
${mail_content}
</div>
</html>