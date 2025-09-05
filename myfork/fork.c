#include <stdio.h>
#include <sys/types.h>
#include <unistd.h>

int main(){
	pid_t pids[10];
	int n=10;
	int i =0;

	for(i=0;n<10;i++){
		pids[i]=fork();

		if(pid[i] < 0){
			printf("fork failed");

		}else if(pid[i] ==0){

	
