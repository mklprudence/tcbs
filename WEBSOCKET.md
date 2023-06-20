# [TCBS] Websocket Protocol for TCBS

## Protocol for message content
TCBS network uses plaintext for message content. ALL messages are prefixed with a tag inside square brackets, eg. ```[INIT]```, to indicate message type. Such tags and types are listed below

## onOpen
Upon completing WS handshake and establishing connection, clients are encouraged to introduce themselves to the server via a [initializing message](#init-initialization-init). 


## Client messages
### [INIT] Initialization
Syntax: ```{clientID}```
| field | Explanation |
| ----- | ----------- |
| clientID | client identifiers uses the namespace structure, `{namespace}:{name}`. There are three namespaces available in TCBS (`turtle`, `controller`, `spectator`) |

