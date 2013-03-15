#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <string.h>

int main(void){

    /*Variables declarations*/
    /*Process*/
    pid_t pid1, pid2;
    int fd[2], i;
    /* Strings */
    char ligne[256], formatedInput[256], inputBuffer[20], *unformatedGlobalInput, lineSize[3];
    /* Stream */
    FILE* input;
    int inputSize;
    /*Dev vars*/


    /*Pipe Creation*/
    if(pipe(fd) == -1){
        perror("pipe error");
        return 1;
    }

    /*Process creation*/
    pid1 = fork();
    switch(pid1){
        case -1: /* error gestion */
            perror("fork1 error");
            break;

        case 0: /* child process 1*/

            /* Set stream & pipe stetting*/
            input = fopen("input.txt", "r");
            close(fd[0]);

            /*Body child process 1*/
            while(fgets(ligne, 256, input) != NULL){

                /* stream formatting */
                ligne[strlen(ligne)-1] = 0;
                sprintf(formatedInput, "[%03d][%s]", (int)strlen(ligne), ligne);

                /* Send */
                write(fd[1], formatedInput, (int)strlen(formatedInput));
                printf(" Process child 1 - stream sended: %s\n",formatedInput);
            }
            /*Close input out*/
            close(fd[1]);
            return 0;
            break;

        default: /* Parent process */
            /*Process creation*/
            pid2 = fork();
            switch(pid2){
                case -1: /* error gestion */
                    perror("fork2 error");
                    break;
                case 0: /* child process 2*/
                    /* Waiting the pipe filling */
                    sleep(1);

                    /*Close input out*/
                    close(fd[1]);

                    /* Input concatenation */
                    while( inputSize = read(fd[0],inputBuffer, sizeof(inputBuffer))){
                        if(inputSize != 20){
                            unformatedGlobalInput = realloc (unformatedGlobalInput, sizeof(inputSize) + sizeof(unformatedGlobalInput));
                            strncat(unformatedGlobalInput, inputBuffer, inputSize);
                            printf("\n****Process child 2 - End of global input stream concatenation : ****\n\n%s\n", unformatedGlobalInput);}
                        else{
                            unformatedGlobalInput = realloc (unformatedGlobalInput, sizeof(inputBuffer) + sizeof(unformatedGlobalInput));
                            strcat(unformatedGlobalInput, inputBuffer);
                        }
                    }

                    /* Pipe closing*/
                    close(fd[0]);

                    printf("\n****Process child 2 - Formated output : \n\n");
                    /*Output formatting*/
                    while(*unformatedGlobalInput != '\0' ){
                        /*Get line size*/
                            unformatedGlobalInput++;
                        for (i = 0; i < 3; i++){
                            lineSize[i] = *unformatedGlobalInput;
                            unformatedGlobalInput++;
                        }

                        /*Send lines to output*/
                        unformatedGlobalInput++;
                        unformatedGlobalInput++;
                        printf(">>>");
                        int line = atoi(lineSize);
                        for (i = atoi(lineSize) ; i > 0 ; i--){
                            printf("%c",*unformatedGlobalInput);
                            unformatedGlobalInput++;
                        }
                        printf("<<<\n");
                        unformatedGlobalInput++;
                    }
                    break;
                default: /*Parent process*/
                    /*
                       close(p[0]);
                       close(p[1]);
                       waitpid(pid1,,WUNTRACED);
                       waitpid(pid2,,WUNTRACED);
                       */
                    return 0;
                    break;
            }
    }
    return 0;
}
