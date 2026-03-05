@{
  # Linux server info
  ServerHost = '66.63.173.143'
  ServerUser = 'root'
  SshPort = 22

  # Repo path on server
  ServerAppRoot = '/root/website'
  RemoteScriptPath = '/root/website/deploy/server/deploy-all.sh'

  # Default deploy branch
  Branch = 'main'

  # Optional private key path (empty = use ssh agent/default key)
  KeyPath = ''
}
