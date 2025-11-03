#include <stdio.h>
#include <pthread.h>
#include <semaphore.h>
#include <unistd.h>
#include <stdlib.h>
#include <time.h>

#define READER_COUNT 990
#define WRITER_COUNT 10
#define TOTAL_THREADS 1000

int x = 0;
int read_count = 0;

sem_t r_mutex;  // lock for readers and writers
sem_t mutex;    // protect read_count
sem_t turn;

void *reader(void *arg) {
    sem_wait(&turn);
    sem_wait(&mutex);
    read_count++;
    if (read_count == 1)
        sem_wait(&r_mutex);
    sem_post(&mutex);
    sem_post(&turn);
    // critical section
    sleep(1);

    sem_wait(&mutex);
    read_count--;
    if (read_count == 0)
        sem_post(&r_mutex);
    sem_post(&mutex);

    return NULL;
}

void *writer(void *arg) {
    int id = *((int *) arg);
    free(arg);
    sem_wait(&turn);
    sem_wait(&r_mutex);

    sleep(1);
    x++;
    printf("Writer %d updated x = %d\n", id, x);
    sem_post(&r_mutex);
    sem_post(&turn);
    return NULL;
}
int comp(const void *a, const void *b) {
    return (*(int *)a - *(int *)b);
}
int main() {
    pthread_t threads[TOTAL_THREADS];
    int writer_indices[WRITER_COUNT];
    time_t start_t, end_t;
    srand(time(NULL));

    // random writer positions
    for (int i = 0; i < WRITER_COUNT; i++) {
        int r;
        do {
            r = rand() % TOTAL_THREADS;
            printf("Generated writer index: %d\n", r);
            for (int j = 0; j < i; j++)
                if (writer_indices[j] == r)
                    r = -1;
        } while (r == -1);
        writer_indices[i] = r;
    }
    for (int i= 0 ;i< WRITER_COUNT; i++) {
        printf("Writer %d at index %d\n", i, writer_indices[i]);
    }

    sem_init(&r_mutex, 0, 1);
    sem_init(&mutex, 0, 1);
    sem_init(&turn, 0, 1);
    int curr = 0;
    qsort(writer_indices, WRITER_COUNT, sizeof(int), comp);
    time(&start_t);	
    for (int i = 0; i < TOTAL_THREADS; i++) {
        int *id = malloc(sizeof(int));
        *id = i;
        if (curr < WRITER_COUNT && i == writer_indices[curr]) {
            pthread_create(&threads[i], NULL, writer, id);
            curr++;
            printf("Created writer thread at index %d\n", i);
        } else {
            pthread_create(&threads[i], NULL, reader, NULL);
        }
    }

    for (int i = 0; i < TOTAL_THREADS; i++)
        pthread_join(threads[i], NULL);
    time(&end_t);

	printf("Finished in %li seconds.\n", end_t - start_t);
    sem_destroy(&r_mutex);
    sem_destroy(&mutex);
    sem_destroy(&turn);
    printf("Final x = %d\n", x);
    return 0;
}
