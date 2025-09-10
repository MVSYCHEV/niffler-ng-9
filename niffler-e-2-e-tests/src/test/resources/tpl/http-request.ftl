<html>
<#-- @ftlvariable name="data" type="io.qameta.allure.attachment.http.HttpRequestAttachment" -->
<head>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8">
    <link type="text/css" href="https://yandex.st/highlightjs/8.0/styles/github.min.css" rel="stylesheet"/>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/highlight.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/http.min.js"></script>
    <script type="text/javascript" src="https://yandex.st/highlightjs/8.0/languages/json.min.js"></script>
    <script type="text/javascript">hljs.initHighlightingOnLoad();</script>
    <style>
        pre {
            white-space: pre-wrap;
        }
    </style>
</head>
<body>
<h5>HTTP Request</h5>
<div>
    <pre><code class="language-http">${data.method} ${data.url}
<#if data.headers??>
<#list data.headers as header>${header.name}: ${header.value}
</#list></#if>
<#if data.body??>
${data.body}</#if></code></pre>
</div>
</body>
</html>