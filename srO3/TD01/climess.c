#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/ipc.h>
#include <unistd.h>
#include "./shop.h"
int main(void){

    int id_msg, long_msg =sizeof(message);
    message msg;
    key_t key;
    /*Creation de la key*/
    key = ftok("./sr03p043.txt", 0);
    if(key == -1){
        perror("ftok failed");
        return 1;
    }

    /* Connetion à la file
     * IPC_CREAT -> creation de file
     * IPC_EXCL -> file excusive (unique)
     */
    id_msg = msgget(key, 0);
    if( id_msg == -1){
        perror("msgget failed");
        return 2;
    }

    /*Client routine*/
e1:
    /*Identification*/
    msg.type = request;
    msg.req = getClientId;
    msgsnd(id_msg, (void*)&msg, long_msg, 0);
    msg.clientId = 2;
    printf("toto %d\n",msg.clientId);
    sleep(2);
    msgrcv(id_msg, (void*)&msg, long_msg, response, 0);
    printf("Connetion with serveur : OK\nMy id : %d\n", msg.clientId);

    if (msg.clientId == -1){
        printf("J'ai été mis en attente par le serveur ! \n");
        sleep(2);
        goto e1;
    }

    /* Get products list*/
    msg.type = request;
    msg.req = getList;
    msgsnd(id_msg, (void*)&msg, long_msg, 0 );
    msgrcv(id_msg, (void*)&msg, long_msg, msg.clientId, 0);
    printf("La listes des produits a été récuperée \n **Produits**\n  1: Pommes \n2: Patates\n0: Quitter\n");

    int choix;
    /*Choix details*/
    while(choix != 0){
        printf("\n\n ** Menu **\n");
        printf(" Les détails sur les produits dispos \n*******\n");
        scanf("%d", &choix);
        switch(choix){
            case 1:
                printf("Pommes - Prix: %d, Quantité: %d\n ", msg.stock[0].price, msg.stock[0].quantity);
                break;
            case 2:
                printf("Patates - Prix: %d, Quantité: %d\n ", msg.stock[1].price, msg.stock[1].quantity);
                break;
            case 0:
                msg.type = request;
                msg.req = cltServd;
                msgsnd(id_msg, (void*)&msg, long_msg, 0 );
                msgrcv(id_msg, (void*)&msg, long_msg, msg.clientId, 0);
                break;
            default:
                printf("Choix invalide\n");
                break;
        }
    }

    /*Autre request*/
    /*Envoi de message de sortie de file*/

    /*ipcrm pour delete file*/
    return 0;
}

