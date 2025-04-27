# Configurare login con Microsoft (Azure AD)

### 1 Registra un'app in Microsoft Azure
- Vai su: https://portal.azure.com/
- Cerca **Azure Active Directory** > **App registrations** > **New registration**.
- Inserisci:
    - **Name**: quello che vuoi
    - **Redirect URI**: ancora una volta il redirect di Keycloak, tipo:

      ```
      https://<keycloak-host>/realms/<realm-name>/broker/azure/endpoint
      ```

- Dopo la registrazione:
    - Copia **Application (client) ID**.
    - Copia **Directory (tenant) ID**.

- Vai su **Certificates & Secrets** → **New Client Secret** → copia anche il secret.

---

### 2 Configura Microsoft in Keycloak
- Vai su Keycloak Admin Console.
- **Identity Providers** → **Add Provider** → **OIDC v1.0** (generic).
- Configura:
    - **Alias**: `azure`
    - **Display Name**: "Microsoft"
    - **First Login Flow**: `first broker login`
    - **Authorization URL**:
      ```
      https://login.microsoftonline.com/<tenant-id>/oauth2/v2.0/authorize
      ```
    - **Token URL**:
      ```
      https://login.microsoftonline.com/<tenant-id>/oauth2/v2.0/token
      ```
    - **User Info URL**:
      ```
      https://graph.microsoft.com/oidc/userinfo
      ```
    - **Client ID**: quello di Azure
    - **Client Secret**: quello di Azure
    - **Scopes**: `openid email profile`
    - **Default Scopes**: `openid email profile`

- Salva.

https://console.cloud.google.com/