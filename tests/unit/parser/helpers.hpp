#pragma once

#include "antlr4-runtime.h"
#include "ast/Nodes.hpp"

using namespace lentz;

// --- Error collector to capture parser errors for negative tests ---
struct CollectingErrorListener : antlr4::BaseErrorListener {
    std::vector<std::string> messages;
    void syntaxError(antlr4::Recognizer * /*rec*/,
                     antlr4::Token * /*offendingSymbol*/,
                     size_t line, size_t charPos,
                     const std::string &msg,
                     std::exception_ptr /*e*/) override {
        messages.push_back(std::to_string(line) + ":" + std::to_string(charPos) + " " + msg);
    }
};

// Positive-path convenience: parse via Frontend and return Program
static std::unique_ptr<ast::Program> parseOK(const std::string& text) {
    frontend::Frontend fe;
    frontend::Source src{"mem", text};
    return fe.parse(src);
}

// Negative path: collect both lexer & parser errors
static std::vector<std::string> parseErrors(const std::string& text) {
    antlr4::ANTLRInputStream input(text);
    OolongLexer lexer(&input);

    CollectingErrorListener listener;
    lexer.removeErrorListeners();          // suppress ConsoleErrorListener
    lexer.addErrorListener(&listener);

    antlr4::CommonTokenStream tokens(&lexer);
    OolongParser parser(&tokens);
    parser.removeErrorListeners();         // suppress ConsoleErrorListener
    parser.addErrorListener(&listener);

    (void)parser.compilationUnit();
    return listener.messages;
}