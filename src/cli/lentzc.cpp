#include <filesystem>
#include <fstream>
#include <iostream>
#include <string>

#include "lentz/util/Diagnosis.hpp"

namespace fs = std::filesystem;

int main(int argc, char** argv) {
    if (argc < 3) {
        std::cerr << "Usage: oolongc <input.ol> <out-dir>\n";
        return 2;
    }
    fs::path in = argv[1];
    fs::path out = argv[2];

    std::ifstream ifs(in);
    if (!ifs) { lentz::util::error("Cannot open input: " + in.string()); return 1; }
    std::string srcText((std::istreambuf_iterator<char>(ifs)), std::istreambuf_iterator<char>());

    /*lentz::frontend::Frontend fe;
    lentz::frontend::Source src{in.string(), srcText};
    auto prog = fe.parse(src);

    lentz::analysis::SymbolTable syms;
    lentz::analysis::predeclareClasses(syms, *prog);

    lentz::backend::CppEmitter emitter;
    emitter.emit(*prog, {out});

    std::cerr << "[next] Now compile the generated C++: \n  c++ -std=c++20 "
              << (out / "main.cpp").string() << " -o " << (out / "a.out").string() << "\n";
    */
    return 0;

}