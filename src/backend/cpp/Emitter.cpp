#include "lentz/backend/Emitter.hpp"
#include "lentz/util/Diagnosis.hpp"
#include <fstream>

namespace fs = std::filesystem;

namespace lentz::backend {

    static void openNamespaces(std::ostream& os, const std::vector<std::string>& ns) {
        for (auto const& n : ns) os << "namespace " << n << " {\n";
    }
    static void closeNamespaces(std::ostream& os, const std::vector<std::string>& ns) {
        for (size_t i = 0; i < ns.size(); ++i) os << "}\n";
    }

    void CppEmitter::emit(const lentz::ast::Program& prog, const EmitOptions& opts) {
        fs::create_directories(opts.outDir);
        std::ofstream main(opts.outDir / "main.cpp");

        main << "#include <iostream>\n";

        /*// Inside package namespace: define an entry helper
        if (prog.package && !prog.package->name.empty()) {
            openNamespaces(main, prog.package->name);
        }

        main << "static int __oolong_entry() {\n";
        main << "  std::cout << \"Hello from Oolong! Classes: ";
        for (size_t i = 0; i < prog.classes.size(); ++i) {
            main << prog.classes[i]->name;
            if (i + 1 < prog.classes.size()) main << ",";
        }
        main << "\" << std::endl;\n";
        main << "  return 0;\n";
        main << "}\n";

        if (prog.package && !prog.package->name.empty()) {
            closeNamespaces(main, prog.package->name);
        }

        // Global main calls the namespaced helper
        main << "int main() {\n";
        if (prog.package && !prog.package->name.empty()) {
            // Build qualified name: demo::...::__oolong_entry()
            for (auto const& n : prog.package->name) main << n << "::";
        }
        main << "__oolong_entry();\n";
        main << "  return 0;\n";
        main << "}\n";
*/
        util::info("Emitted C++ to " + opts.outDir.string());
    }

} // namespace oolong::codegen