<#macro registrationLayout displayInfo=false displayMessage=true; section>
  <!-- header -->
  <#if section == "header">
    <h1>Login</h1>

  <!-- form -->
  <#elseif section == "form">
    <form>...</form>

  <!-- footer -->
  <#elseif section == "footer">
    <footer>...</footer>
  </#if>
</#macro>
