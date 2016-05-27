; Properties you define here will be visible in all environments
; Of course you can override them in individual environments
{:mt {:as                 :json
       :max-connections    10
       :connection-timeout 2000
       :request-timeout    5000
       :prefix-url         "http://ip1.uat.iggroup.local/markettaxonomy"}
 :cs  {:as                 :json
       :max-connections    10
       :connection-timeout 2000
       :request-timeout    5000
       :prefix-url         "http://ep1.uat.iggroup.local/clientsentiment"}}