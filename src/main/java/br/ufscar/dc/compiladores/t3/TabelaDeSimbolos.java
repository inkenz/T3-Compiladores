package br.ufscar.dc.compiladores.t3;

import java.util.HashMap;


public class TabelaDeSimbolos {
    private HashMap<String, EntradaTabelaDeSimbolos> tabelaDeSimbolos;
    
    public TabelaDeSimbolos(){
        tabelaDeSimbolos = new HashMap<>();
    }
    
    public void inserir(String nome, double valor){
        EntradaTabelaDeSimbolos etds = new EntradaTabelaDeSimbolos();
        etds.nome = nome;
        etds.valor = valor;
        tabelaDeSimbolos.put(nome, etds);
    }
    
    public EntradaTabelaDeSimbolos verificar(String nome){
        return tabelaDeSimbolos.get(nome);
    }
}
