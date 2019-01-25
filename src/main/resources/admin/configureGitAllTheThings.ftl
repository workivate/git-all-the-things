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

    [@ww.textfield label="Token" name="token" description="A personal API token on GitHub"/]

    [@ww.textfield label="WebHook Secret" name="secret" description="The webhook secret on GitHub. <a href='http://www.guidgen.com/' target='_blank'>Generate a secrete GUID</a>"/]

    [@ww.textfield label="Bot Name" name="botName" description="The display name for triggered builds"/]

[/@s.form]

<h1>Instructions</h1>
<p>This global configuration is used by ALL pull requests and must be configured in order to build pull requests.</p>

<ol>
    <li>
        Generate a <a href="https://github.com/settings/tokens" target="_blank">GitHub personal access token</a>
        with the <strong>repo:status</strong> scope. This token is used to update pull requests
        status after a build.
    </li>
    <li>
        <em>Token</em> is your personal access token from step 1.
    </li>
    <li>
        <p>
            Navigate to your GitHub repository settings and add a new web hook. GitHub calls this
            hook when a pull request is created or modified. If a branch build does not exist
            for the pull request, one will be created.
        </p>
        <ul>
            <li>
                <em>Payload URL</em> is http://BAMBOO_URL/bamboo/rest/github-webhook/1.0/pullrequest-trigger/PLAN_KEY.
                Replace <strong>BAMBOO_URL</strong> with your Bamboo installation URL (including the port) and
                <strong>PLAN_KEY</strong> with the plan key where pull request branch builds should be created.
                Depending on your Bamboo installation you may need to omit <em>/bamboo</em> from the URL.
            </li>
            <li>
                <em>Content Type</em> is <strong>application/json</strong>.
            </li>
            <li>
                <em>Secret</em> is an arbitrary string, though a GUID is recommended. It is used by Bamboo
                to verify requests to the web hook. <a href="http://www.guidgen.com/" target="_blank">Generate a Guid</a>
            </li>
            <li>
                Select <strong>Let me select individual events</strong> then check <strong>Pull Request</strong>. Only Pull Request should be checked, so uncheck all other events.
            </li>
        </ul>
    </li>
    <li>
        <em>Secret</em> is the secrete value from step 3.
    </li>
    <li>
        <em>Bot Name</em> is the name displayed in Bamboo as the user which triggered a build when a pull request is
        created or updated.
    </li>
    <li>
        Ensure your Bamboo build plan has View permissions for anonymous users.
        Navigate to the plan configuration, permissions tab. Tick the box so that <em>Other Users</em> are permitted to <em>View</em>.
    </li>
</ol>
</body>
</html>