package br.ufscar.dc.compiladores.t3;

import java.util.ArrayList;
import java.util.List;
import org.antlr.v4.runtime.Token;
import br.ufscar.dc.compiladores.t3.LAParser.TipoContext;
import br.ufscar.dc.compiladores.t3.LAParser.Tipo_basicoContext;
import br.ufscar.dc.compiladores.t3.LAParser.Tipo_estendidoContext;
import br.ufscar.dc.compiladores.t3.LAParser.VariavelContext;
import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.Tipo;

public class LinguagemLAUtils {
    public static List<String> errosSemanticos = new ArrayList<>();
    
    public static void adicionarErroSemantico
    (
        Token t,
        String mensagem
    ) 
    {
        int linha = t.getLine();
        errosSemanticos.add(String.format("Linha %d: %s", linha, mensagem));
    }

    public static Tipo verificarTipo
    (
        TabelaDeSimbolos tabela,
        Tipo_basicoContext ctx)
    {
        if (ctx.LITERAL() != null){
            return Tipo.LITERAL;
        }
        else if (ctx.INTEIRO() != null){
            return Tipo.INTEIRO;
        }
        else if (ctx.LOGICO() != null){
            return Tipo.LOGICO;
        }
        else if (ctx.REAL() != null){
            return Tipo.REAL;
        }
        else {
            return Tipo.INVALIDO;
        }
    }

    public static Tipo verificarTipo
    (
        TabelaDeSimbolos tabela,
        Tipo_estendidoContext ctx
    )
    {
        Tipo tipo;

        // Caso haja o simbolo de ponteiro antes é declarado como ponteiro.
        if (ctx.PONTEIRO() != null){
            return Tipo.PONTEIRO;
        }

        // Caso seja um identificador, é um registro,
        // então é necessário ver se o tipo de registro existe.
        else if (ctx.tipo_basico_ident().IDENT() != null) {
            if (!tabela.existe(ctx.tipo_basico_ident().IDENT().getText())){
                return Tipo.INVALIDO;
            }
            else{
                tipo = Tipo.REGISTRO;
            }
        }
        
        // É uma variável de tipo básico.
        else {
            tipo = verificarTipo(tabela, ctx.tipo_basico_ident().tipo_basico());
        }

        
        return tipo;
    }

    public static Tipo verificarTipo
    (
        TabelaDeSimbolos tabela,
        TipoContext ctx
    )
    {
        // if (ctx.tipo_variavel() != null){
            return verificarTipo(tabela, ctx.tipo_estendido());
        // }
        // else{
        //     return verificarTipo(tabela, ctx.registro());
        // }
    }

    public static Tipo verificarTipo
    (
        TabelaDeSimbolos tabela,
        VariavelContext ctx
    )
    {
        Tipo tipo = verificarTipo(tabela, ctx.tipo());

        ctx.identificador().forEach(ident -> {
            if (tabela.existe(ident.getText())){
                adicionarErroSemantico(
                    ctx.tipo().start,
                    "identificador " + ident.getText() + " ja declarado anteriormente"
                    );
            }
            else{
                tabela.inserir(ident.getText(), tipo);
            }
        });

        if (tipo == Tipo.INVALIDO){
            adicionarErroSemantico(ctx.tipo().start, "tipo " + ctx.tipo().getText() + " nao declarado" );
        }

        return tipo;
    }
}
