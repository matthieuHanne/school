#define request 1
#define response 20
#define cltServd 3
#define getClientId 5
#define getList 6


typedef struct{
    int type;
    int price;
    int quantity;
}products;

typedef struct {
    long type;
    int req;
    int clientId;
    products stock[2];
}message;



