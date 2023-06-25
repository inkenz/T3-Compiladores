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
    public Void visitDeclaracao_global(LAParser.Declaracao_globalContext ctx)
    {
        TabelaDeSimbolos tabelaAtual = escopos.escopoAtual();

        if (ctx.PROCEDIMENTO() != null){
            String nome = ctx.PROCEDIMENTO().getText();

            // TODO: Colocar erro semântico caso o nome do procedimento já exista.
            if (tabelaAtual.existe(nome)){
                
            }
        }
        else if (ctx.FUNCAO() != null){
            String nome = ctx.FUNCAO().getText();
            
            // TODO: Colocar erro semântico caso o nome do procedimento já exista.
            if (tabelaAtual.existe(nome)){
            }
        }

        return super.visitDeclaracao_global(ctx);
    }

    @Override
    public Void visitDeclaracao_local(LAParser.Declaracao_localContext ctx)
    {
        TabelaDeSimbolos tabela = escopos.escopoAtual();

        if (ctx.DECLARE() != null){
            String nome = ctx.DECLARE().getText();

            // Já existe a variável.
            if (tabela.existe(nome)){
                System.out.println("Variavel " + nome + "ja esta declarada");
            }
            else{
                Tipo tipo = LinguagemLAUtils.verificarTipo(tabela, ctx.variavel());
                
            }
        }
        return super.visitDeclaracao_local(ctx);
    }

    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx) {
        Tipo tipoExp = verificarTipo(escopos, ctx.expressao());
        boolean error = false;
        String nomeVar = ctx.identificador().getText();
        //System.out.print(ctx.getText() +"   ");
        //System.out.print(tipoExp+"\n");
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

        if(error)
            adicionarErroSemantico(ctx.identificador().start, "atribuicao nao compativel para " + nomeVar );

        return super.visitCmdAtribuicao(ctx);
    }

    
    
    
    
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
