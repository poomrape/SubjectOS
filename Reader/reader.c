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

void *reader(void *arg) {
    sem_wait(&mutex);
    read_count++;
    if (read_count == 1)
        sem_wait(&r_mutex);
    sem_post(&mutex);

    // critical section
    sleep(1);
    printf("Reader reading x = %d\n", x);

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

    sem_wait(&r_mutex);
    sleep(1);
    x++;
    printf("Writer %d updated x = %d\n", id, x);
    sem_post(&r_mutex);

    return NULL;
}

int main() {
    pthread_t threads[TOTAL_THREADS];
    int writer_indices[WRITER_COUNT];

    srand(time(NULL));

    // random writer positions
    for (int i = 0; i < WRITER_COUNT; i++) {
        int r;
        do {
            r = rand() % TOTAL_THREADS;
            for (int j = 0; j < i; j++)
                if (writer_indices[j] == r)
                    r = -1;
        } while (r == -1);
        writer_indices[i] = r;
    }

    sem_init(&r_mutex, 0, 1);
    sem_init(&mutex, 0, 1);

    int curr = 0;
    for (int i = 0; i < TOTAL_THREADS; i++) {
        int *id = malloc(sizeof(int));
        *id = i;
        if (curr < WRITER_COUNT && i == writer_indices[curr]) {
            pthread_create(&threads[i], NULL, writer, id);
            curr++;
        } else {
            pthread_create(&threads[i], NULL, reader, NULL);
            free(id);
        }
    }

    for (int i = 0; i < TOTAL_THREADS; i++)
        pthread_join(threads[i], NULL);

    sem_destroy(&r_mutex);
    sem_destroy(&mutex);
    printf("Final x = %d\n", x);
    return 0;
}
