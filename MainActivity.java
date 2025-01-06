package com.example.hotelapp;

// Importa as classes necessárias para trabalhar com banco de dados e contexto no Android
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

// Classe que gerencia o banco de dados do aplicativo
public class DatabaseHelper extends SQLiteOpenHelper {

    // Nome do banco de dados e versão
    private static final String DATABASE_NAME = "hotel.db"; // Nome do arquivo do banco de dados
    private static final int DATABASE_VERSION = 2;          // Versão do banco de dados (usado para atualizações)

    // Nome da tabela e colunas
    private static final String TABLE_NAME = "hospedes";        // Nome da tabela
    private static final String COLUMN_ID = "id";               // Coluna para o ID único de cada hóspede
    private static final String COLUMN_NAME = "nome";           // Coluna para o nome do hóspede
    private static final String COLUMN_QUARTO = "quarto";       // Coluna para o número do quarto
    private static final String COLUMN_DIAS = "dias";           // Coluna para o número de dias de hospedagem
    private static final String COLUMN_PAGAMENTO = "pagamento"; // Coluna para a forma de pagamento
    private static final String COLUMN_CAFE = "cafe";           // Coluna para indicar se inclui café da manhã

    // Construtor da classe que inicializa o banco de dados
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Criação da tabela no banco de dados
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + // ID autoincrementado
                COLUMN_NAME + " TEXT NOT NULL, "                   + // Nome obrigatório
                COLUMN_QUARTO + " TEXT NOT NULL, "                 + // Quarto obrigatório
                COLUMN_DIAS + " INTEGER NOT NULL, "                + // Dias obrigatórios
                COLUMN_PAGAMENTO + " TEXT NOT NULL, "              + // Pagamento obrigatório
                COLUMN_CAFE + " INTEGER NOT NULL)";                  // Café (1 para sim, 0 para não)
        db.execSQL(CREATE_TABLE);                                    // Executa o comando SQL para criar a tabela
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Atualiza o banco de dados quando a versão é incrementada
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME); // Apaga a tabela existente
        onCreate(db);                                     // Cria uma nova tabela
    }

    // Insere um novo hóspede no banco de dados
    public boolean insertHospede(String nome, String quarto, int dias, String pagamento, boolean cafe) {
        SQLiteDatabase db = this.getWritableDatabase(); // Obtém o banco de dados em modo escrita
        ContentValues values = new ContentValues();     // Estrutura para armazenar os valores
        values.put(COLUMN_NAME, nome);                  // Insere o nome
        values.put(COLUMN_QUARTO, quarto);              // Insere o quarto
        values.put(COLUMN_DIAS, dias);                  // Insere o número de dias
        values.put(COLUMN_PAGAMENTO, pagamento);        // Insere o método de pagamento
        values.put(COLUMN_CAFE, cafe ? 1 : 0);          // Insere 1 para café incluído, 0 caso contrário

        long result = db.insert(TABLE_NAME, null, values); // Insere os valores na tabela
        return result != -1;                               // Retorna true se a inserção foi bem-sucedida
    }

    // Atualiza as informações de um hóspede no banco de dados
    public boolean updateHospede(int id, String nome, String quarto, int dias, String pagamento, boolean cafe) {
        SQLiteDatabase db = this.getWritableDatabase(); // Obtém o banco de dados em modo escrita
        ContentValues values = new ContentValues();     // Estrutura para armazenar os novos valores
        values.put(COLUMN_NAME, nome);                  // Atualiza o nome
        values.put(COLUMN_QUARTO, quarto);              // Atualiza o quarto
        values.put(COLUMN_DIAS, dias);                  // Atualiza o número de dias
        values.put(COLUMN_PAGAMENTO, pagamento);        // Atualiza o método de pagamento
        values.put(COLUMN_CAFE, cafe ? 1 : 0);          // Atualiza a inclusão do café

        int result = db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}); // Atualiza a tabela
        return result > 0; // Retorna true se a atualização foi bem-sucedida
    }

    // Deleta um hóspede do banco de dados usando o ID
    public boolean deleteHospede(int id) {
        SQLiteDatabase db = this.getWritableDatabase();                                           // Obtém o banco de dados em modo escrita
        int result = db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)}); // Exclui o registro pelo ID
        return result > 0;                                                                        // Retorna true se a exclusão foi bem-sucedida
    }

    // Recupera todos os hóspedes do banco de dados
    public ArrayList<String> getAllHospedes() {
        ArrayList<String> hospedes = new ArrayList<>(); // Lista para armazenar os hóspedes
        SQLiteDatabase db = this.getReadableDatabase(); // Obtém o banco de dados em modo leitura
        String query = "SELECT * FROM " + TABLE_NAME;   // Consulta para buscar todos os registros
        Cursor cursor = db.rawQuery(query, null);       // Executa a consulta e retorna os resultados

        if (cursor.moveToFirst()) { // Verifica se há registros
            do {
                // Constrói uma string formatada com os dados do hóspede
                String hospede = cursor.getInt(0) + " | "  + // ID
                        cursor.getString(1) + " | "        + // Nome
                        cursor.getString(2) + " quarto | " + // Quarto
                        cursor.getInt(3) + " dias | "      + // Dias
                        cursor.getString(4) + " | "        + // Pagamento
                        "Café: " + (cursor.getInt(5) == 1 ? "Sim" : "Não"); // Café incluído ou não
                hospedes.add(hospede);                      // Adiciona a string à lista
            } while (cursor.moveToNext());                  // Move para o próximo registro
        }

        cursor.close();  // Fecha o cursor
        return hospedes; // Retorna a lista de hóspedes
    }
}