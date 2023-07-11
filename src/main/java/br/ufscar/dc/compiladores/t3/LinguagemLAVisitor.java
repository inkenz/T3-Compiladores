package br.ufscar.dc.compiladores.t3;

import static br.ufscar.dc.compiladores.t3.LinguagemLAUtils.adicionarErroSemantico;
import static br.ufscar.dc.compiladores.t3.LinguagemLAUtils.verificarTipo;
import br.ufscar.dc.compiladores.t3.TabelaDeSimbolos.Tipo;
import java.util.LinkedList;
public class LinguagemLAVisitor extends LABaseVisitor<Void>{
    Escopo escopos = new Escopo();

    @Override
    public Void visitDeclaracoes(LAParser.DeclaracoesContext ctx) 
    {
        escopos.criarNovoEscopo();
        return super.visitDeclaracoes(ctx);
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx)
    {
        TabelaDeSimbolos tabela = escopos.escopoAtual();

        System.out.println("LOCAL\n");

        if (ctx.DECLARE() != null){
            verificarTipo(tabela, ctx.variavel());       
        }
        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitRegistro(LAParser.RegistroContext ctx){
        TabelaDeSimbolos tabela = escopos.escopoAtual();

        System.out.println("REGISTRO\n");

        Tipo tipoSla = verificarTipo(tabela, ctx.variavel());
        System.out.println("Aka -> " + tipoSla);

        //System.out.println("Aka -> " + ctx.variavel().get(0).getText());

        return super.visitRegistro(ctx);
    }

    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        Tipo tipoExp = verificarTipo(escopos, ctx.expressao());
        boolean error = false;
        String nomeVar = ctx.identificador().getText();
        
        if (tipoExp != Tipo.INVALIDO) {
            for(TabelaDeSimbolos escopo : escopos.recuperarTodosEscopos()){
                if (escopo.existe(nomeVar))  {
                    Tipo tipoVar = verificarTipo(escopos, nomeVar);
                    Boolean varNumeric = tipoVar== Tipo.REAL || tipoVar == Tipo.INTEIRO;
                    Boolean expNumeric = tipoExp == Tipo.REAL || tipoExp == Tipo.INTEIRO;
                    
                    if  (!(varNumeric && expNumeric) && tipoVar != tipoExp && tipoExp != Tipo.INVALIDO) {
                        error = true;
                    }
                } 
            }
        } else{
            error = true;
        }

        if(error){
            if(ctx.PONTEIRO() != null)
                adicionarErroSemantico(ctx.identificador().start, "atribuicao nao compativel para ^" + nomeVar );
            else
                adicionarErroSemantico(ctx.identificador().start, "atribuicao nao compativel para " + nomeVar );
        }

        return super.visitCmdAtribuicao(ctx);
    }

    /*@Override
    public Void visitTipo_estendido(LAParser.Tipo_estendidoContext ctx){
        if(ctx.PONTEIRO() != null){
            String nome = ctx.PONTEIRO().getText();
            System.out.println("AQUI -> " + nome);
            for(TabelaDeSimbolos escopo : escopo.recuperarTodosEscopos()){
                if(escopo.existe)
            }
        }


        return super.visitTipo_estendido(ctx);
    }*/
    
    
    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) 
    {
        LinkedList<TabelaDeSimbolos> tabelas = escopos.recuperarTodosEscopos();
        String nome = ctx.IDENT().get(0).getText();
        boolean existeVariavel = false;

        for ( TabelaDeSimbolos tabela: tabelas){
            if (tabela.existe(nome)){
                existeVariavel = true;
                break;
            }
        }

        if (!existeVariavel){
            adicionarErroSemantico(ctx.start, "identificador " + nome + " nao declarado" );
        }

        return super.visitIdentificador(ctx);
    }
}
