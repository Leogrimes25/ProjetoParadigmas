import com.mongodb.ErrorCategory;
import com.mongodb.MongoWriteException;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import java.util.Scanner;
public class ProjetoVersion1 {
    public static void main(String[] args) {
        MongoClient mongoClient = MongoClients.create("mongodb://localhost:27017/");
        MongoDatabase mongoDatabase = mongoClient.getDatabase("ProjetoParadigmas");
        System.out.println("Conexão Estabelecida");

        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println();
            System.out.println("Escolha uma funcionalidade do Sistema :");
            System.out.println("1. Adição Única de Documento :");
            System.out.println("2. Visualização de Todos os Documentos :");
            System.out.println("3. Visualização Única:");
            System.out.println("4. Atualizar por ObjectId:");
            System.out.println("5. Deletar por ObjectId");
            System.out.println("0. Sair");
            System.out.println("Digite o Número:");


            int choose = scanner.nextInt();

            switch (choose) {
                case 1:
                    singleAddition(mongoDatabase);
                    break;
                case 2:
                    visualizeAll(mongoDatabase);
                    break;
                case 3:
                    visualizeById(mongoDatabase);
                    break;
                case 4:
                    updateById(mongoDatabase);
                    break;
                case 5:
                    deleteById(mongoDatabase);
                    break;
                case 0:
                    System.out.println("Saindo do programa.");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Opção inválida. Tente novamente.");
                    break;
            }
        }
    }

    private static void singleAddition(MongoDatabase mongoDatabase) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Produtos");

        Document produto = new Document();
        produto.append("nome", "Monitor Super Frame")
                .append("marca", "SuperFrame")
                .append("preço", 500.00)
                .append("estoque", 15)
                .append("categoria", "Informática")
                .append("especificacoes", new Document("tamanho", "22 polegadas")
                        .append("resolucao", "1920x1080")
                        .append("taxa_atualizacao", "75Hz"));
        try {
            mongoCollection.insertOne(produto);
            System.out.println("Inserção Concluída !");
        } catch (MongoWriteException mwe) {
            if (mwe.getError().getCategory().equals(ErrorCategory.DUPLICATE_KEY)) {
                System.out.println("Document with that id already exists");
            }
        }
    }



    private static void visualizeAll(MongoDatabase mongoDatabase) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Produtos");
        Bson projection = Projections.excludeId();
        try (MongoCursor<Document> cursor = mongoCollection.find().projection(projection).iterator()){
            while (cursor.hasNext()) {
                System.out.println(cursor.next().toJson());
            }
        }
    }

    private static void visualizeById(MongoDatabase mongoDatabase) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Produtos");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o ObjectId do documento:");
        String objectId = scanner.next();
        ObjectId objectIdFilter = new ObjectId(objectId);
        Document visualizebyId = mongoCollection.find(Filters.eq("_id", objectIdFilter)).first();
        System.out.println("O Documento referido é:");

        if (visualizebyId != null) {
            System.out.println(visualizebyId.toJson());
        } else {
            System.out.println("Documento não encontrado para o ObjectId fornecido.");
        }
    }

    private static void updateById(MongoDatabase mongoDatabase) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Produtos");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o ObjectId do documento para atualização:");
        String objectId = scanner.next();
        ObjectId objectIdFilter = new ObjectId(objectId);
        Document filter = new Document("_id", objectIdFilter);
        Document update = new Document("$set", new Document("categoria", "Monitores"));
        mongoCollection.updateOne(filter, update);
        System.out.println("Atualização Concluída do Documento :"
                +mongoCollection.find(Filters.eq("_id", objectIdFilter)).first());
    }

    private static void deleteById(MongoDatabase mongoDatabase) {
        MongoCollection<Document> mongoCollection = mongoDatabase.getCollection("Produtos");
        Scanner scanner = new Scanner(System.in);
        System.out.println("Digite o ObjectId do documento para exclusão:");
        String objectId = scanner.next();
        ObjectId objectIdFilter = new ObjectId(objectId);
        Document filter = new Document("_id", objectIdFilter);
        mongoCollection.deleteOne(filter);
        System.out.println("Documento Deletado");
    }
}


