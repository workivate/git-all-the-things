[#-- @ftlvariable name="action" type="com.carolynvs.gitallthethings.admin.ConfigureGitAllTheThingsAction" --]
[#-- @ftlvariable name="" type="com.carolynvs.gitallthethings.admin.ConfigureGitAllTheThingsAction" --]

<html>
<head>
    <title>Git All The Things!</title>
    <meta name="decorator" content="adminpage">
</head>
<body>
<h1>Configure Git All The Things!</h1>

[@s.form action="saveGitAllTheThings" namespace="/admin" submitLabelKey="global.buttons.update" ]

    [@ww.textfield labelKey="gitthings.admin.token" name="token" descriptionKey="gitthings.admin.token.description"/]

    [@ww.textfield labelKey="gitthings.admin.secret" name="secret" descriptionKey="gitthings.admin.secret.description"/]

    [@ww.textfield labelKey="gitthings.admin.botName" name="botName" descriptionKey="gitthings.admin.botName.description"/]

[/@s.form]
</body>
</html>