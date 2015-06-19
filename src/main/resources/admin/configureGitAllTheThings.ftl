[#-- @ftlvariable name="action" type="com.carolynvs.gitallthethings.admin.ConfigureGitAllTheThingsAction" --]
[#-- @ftlvariable name="" type="com.carolynvs.gitallthethings.admin.ConfigureGitAllTheThingsAction" --]

<html>
<head>
    <title>Configure the Git All The Things! Plugin</title>
    <meta name="decorator" content="adminpage">
</head>
<body>
<h1>Configure the Git All The Things! Plugin</h1>

[@s.form action="saveGitAllTheThings" namespace="/admin" submitLabelKey="global.buttons.update" ]

    [@ww.textfield labelKey="gitthings.admin.token" name="token" descriptionKey="gitthings.admin.token.description"/]

[/@s.form]
</body>
</html>