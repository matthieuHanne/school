#include <stdlib.h>
#include <stdio.h>
#include <sys/types.h>
#include <sys/ipc.h>
#include <sys/msg.h>
#include <sys/ipc.h>
#include "./shop.h"

int main(void){

    /*Items list*/
    products apples, potatoes;

    apples.type = 0;
    apples.price = 2;
    apples.quantity = 50;

    potatoes.type = 0;
    potatoes.price = 1;
    potatoes.quantity = 500;



    int id_msg, long_msg = sizeof(message);
    key_t key;
    message msg;
    /*Creation de la key*/
    key = ftok("./sr03p043.txt", 0);
    if(key == -1){
        perror("ftok failed");
        return 1;
    }

    /* Creation de la file
     * IPC_CREAT -> creation de file
     * IPC_EXCL -> file excusive (unique)
     */
    id_msg = msgget(key,IPC_CREAT|IPC_EXCL|0666);
    if( id_msg == -1){
        perror("msgget failed");
        return 2;
    }
printf("Server run\n");
    int concurentClient=0, clientIds=200;
    int clientServed = 0;
    while(clientServed < 4){

        msgrcv(id_msg, (void*)&msg, long_msg, request, 0);
        switch (msg.req){
            case getClientId:
                msg.type = response;
                printf("Client req id - Current concurent clitens : %d\n", concurentClient);
                if(concurentClient < 2){
                    msg.clientId = clientIds;
                    clientIds++;
                    concurentClient++;
                }
                else{
                    printf("Invalide id send : -1");
                    msg.clientId = -1;
                }

                printf("Id Send : %d \n", msg.clientId);
                msgsnd(id_msg, (void*)&msg, long_msg, 0);
                break;

            case getList:
                /*code*/
                msg.stock[1] = apples;
                msg.stock[2] = potatoes;
                msg.type = msg.clientId;
                msgsnd(id_msg,(void*)&msg, long_msg, 0);
                break;

            case cltServd:
                printf("Client - %d - served \n ", msg.clientId);
                clientServed ++;
                concurentClient --;
                break;
        }
    }
    /*ipcrm pour delete file*/
    printf("4 clients served : server shutdown");
    return 0;
}
