#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>
#include <stdlib.h>
#include <sys/wait.h>
int main(){
	pid_t pids[10];
	int i ;

	for(i=0;i<10;i++){
		pids[i]=fork();

		if(pids[i] < 0){
			printf("fork failed");

		}else if(pids[i] ==0){
			printf("My child number:%d\n",i);
			exit(0);
		}
	}
	for(i=0;i<10;i++){
		wait(NULL);
	}
	printf("My Parents Terminated");
}
	
