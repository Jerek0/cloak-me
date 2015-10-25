![Cloak me](http://img15.hostingpics.net/pics/884122planche.jpg)
_Maquette initiale_

# Cloak Me

Envoyez vos messages secrets à vos amis en toute sécurité !

## Description

Avec CloakMe, vous pouvez discuter avec vos amis sans craintes. Grâce à des algorithmes de chiffrage poussés, seul vous et votre destinataire serez en mesure de voir les messages. 

Inscrivez vous, choisissez une couleur, changez de pseudo si la génération de celui-ci ne vous plait pas, et lancez des discussions avec vos amis !

### BETA Test (Google Play)
![Cloak me Logo](http://img11.hostingpics.net/pics/755833logoapp464.png)

_Requête en cours d'analyse par Google Play, le lien de la bêta sera partagé ici une fois reçu_

### Screenshots (Nexus 5 & Nexus 7)
[Télécharger le ZIP](http://91.121.120.180/misc/cloakme/cloakme-screenshots.zip "Télécharger le ZIP")

## Fonctionnement

### Code
_Le code est actuellement en cours de "commentage", merci de revenir dans quelques heures/minutes. Promis ce sera mieux ;)_ 

### Technologies utilisées

Le stockage des données s'effectue sur Firebase. L'authentification est entièrement gérée par ce service et n'a pas été modifiée: les mots de passe utilisateurs sont gérés par firebase.

Les librairies externes utilisées sont les suivantes :

* Firebase (stockage de données)
* Picasso (chargement et opérations avec les images)
* ButterKnife (binding des vues)
* scottyab:aes-crypto (chiffrement symétrique)
* madgag.spongycastle
    
### Chiffrage des données

Chaque utilisateur dispose d'un couple de clés publique/privée RSA (chiffrement asymétrique). Ces clés sont stockées en base et permettront aux utilisateurs de s'échanger les clés privées AES (chiffrement symétrique) relatives à chaque discussion.

Bien entendu, le stockage en ligne de la clé privée RSA de l'utilisateur implique un chiffrement de celle-ci. Avant d'être stockée en base, la clé privée est donc chiffrée en AES via une passphrase : le mot de passe de l'utilisateur. Cette opération se fait à l'inscription (d'où la durée d'attente une fois la requête envoyée) et implique (pour le moment) de ne pas pouvoir changer de mot de passe sans contacter le développeur.
Ainsi, seul l'utilisateur est en mesure de récupérer sa clé privée RSA.

### Structure de la BDD

#### Utilisateur

    "users": {
        ...
        "xxxx-xxxx-xxxx-xxxx-xxxx": {
            "avatar": "URL Gravatar",
            "username": "Loquacious-Fox-457",
            "username_lower_case": "loquacious-fox-457",
            "mail": "test@test.fr",
            "color": "-61838",
            "security": {
                "encrypted_private_key": "...",
                "public_key": "...",
                "salt": "..."
            },
            "channels": {
                ...
                "yyyy-yyyy-yyyy-yyyy": {
                    "encrypted_passphrase": "...",
                    "last_message_id": "wwww-wwww-wwww-wwww",
                    "newMessages": false,
                    "salt": "...",
                    "target_uid": "xxxx-xxxx-xxxx-xxxx",
                    "timestamp": 1445764634164
                }
                ...
            }
        }
        ...
    }
    
Voici la structure type d'un utilisateur dans firebase. Inutile de détailler les champs habituels (avatar / username / mail / etc.), en revanche certaines parties peuvent paraître floues.

* username_lower_case : Cet attribut est nécessaire pour permettre de faire une recherche par nom dans l'application (firebase ne permettant pas de filtrer en ignorant la casse)
* color : Ici, un entier est stocké pour représenter la couleur car les fonctions android requièrent ce typage -> plus pratique
* security : Ici sont stockées les clés RSA publique et privée (celle ci est chiffrée en AES grâce au password utilisateur) avec un salt spécifique (converti en String)
* channels : Chaque utilisateur dispose d'une liste de channels auxquels il appartient.
    * encrypted_passphrase : la passphrase qui servira à générer la clé privée AES de la discussion. Elle est ici chiffrée avec la clé publique de l'utilisateur et donc déchiffrable uniquement avec sa clé privée
    
#### Channels

    "channels": {
        ...
        "yyyy-yyyy-yyyy-yyyy": {
            "users": {
                "zzzz-zzzz-zzzz-zzzz-1": "xxxx-xxxx-xxxx-xxxx-xxxx-1",
                "zzzz-zzzz-zzzz-zzzz-2": "xxxx-xxxx-xxxx-xxxx-xxxx-2"
            }
        }
        ...
    }

Chaque channel dispose d'une liste d'utilisateurs : cela permet de savoir quel profil notifier pour les nouveaux messages. C'est également une bonne pratique en vue d'un éventuel passage aux discussions multiples.

#### Messages

    "messages": {
        ...
        "wwww-wwww-wwww-wwww": {
            "author": "xxxx-xxxx-xxxx-xxxx",
            "channel": "yyyy-yyyy-yyyy-yyyy",
            "encrypted_content": "...",
            "timestamp": 1445725107997
        }
        ...
    }

    
Chaque message dispose de l'uid de son auteur, de son channel ainsi que de son contenu chiffré grâce à la clé privée de la discussion. Le timestamp permet d'ordonner les messages chronologiquement.

### Problèmes identifiés et évolutions relatives

Bien que fonctionnelle, l'appli dispose certains points qui restent à améliorer (au moins on les connait) :

* A chaque changement de données, toute la liste est rafraichie, que cela soit sur la liste des discussions ou dans une discussion. Dans l'idéal, une modification sur une discussion (nouveau message par exemple), ne devrait mettre à jour que l'item correspondant dans la liste. Cependant à la vue des contraintes de temps, il a été choisi d'aller au plus simple pour rester pleinnement fonctionnel
* Les droits en lecture / écriture ne sont pas encore configurés dans firebase : n'importe qui peut tout modifier (mais pas déchiffrer !) à sa guise. Ce qui n'est pas souhaitable bien évidemment. Encore une fois, cela est dû à la contrainte de temps mais aussi au manque de connaissance du service Firebase.
* Pas de persistance de session : si on éteint l'appli on doit se reconnecter (l'appli pré-remplit le champ mail cependant :) )
* Pas de système de notifications de nouveaux messages ( du à la contrainte précédente notamment )
* Gestion de la mémoire : il y a une très grosse optimisation à faire sur ce point (onDestroy et compagnie manquant quasiment partout)

    
 