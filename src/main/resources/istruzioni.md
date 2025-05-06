## 1. Configurare login con Google

### 1.1 Crea un progetto su Google Cloud Console

* Vai su: [https://console.cloud.google.com/](https://console.cloud.google.com/)

* Crea un nuovo progetto (o usa uno esistente).

* Vai su **API e Servizi > Credenziali**.

* Clicca su **Crea credenziali > ID client OAuth**.

* Scegli:

    * **Tipo applicazione**: "Web Application"
    * **URI autorizzati di reindirizzamento**: devi mettere il Redirect URI di Keycloak, che è:

      ```
      https://<keycloak-host>/realms/<realm-name>/broker/google/endpoint
      ```

      Esempio:

      ```
      https://auth.example.com/realms/myrealm/broker/google/endpoint
      ```

* Salva il **Client ID** e il **Client Secret**.

---

### 1.2 Configura Google in Keycloak

* Vai in Keycloak Admin Console.
* Seleziona il tuo Realm.
* Vai su **Identity Providers** → **Add Provider** → **Google**.
* Inserisci:

    * **Client ID**: quello che ti ha dato Google.
    * **Client Secret**: idem.
    * **Default Scopes**: `openid email profile`
* Salva.

# 2 Configurare login con Microsoft (Azure AD)

### 2.1 Registra un'app in Microsoft Azure
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

### 2.2 Configura Microsoft in Keycloak
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