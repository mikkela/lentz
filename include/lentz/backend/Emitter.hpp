#pragma once
#include <filesystem>
#include <memory>
#include "lentz/ast/Nodes.hpp"

namespace lentz::backend {

    struct EmitOptions {
        std::filesystem::path outDir;
    };

    class CppEmitter {
    public:
        void emit(const lentz::ast::Program& prog, const EmitOptions& opts);
    };

} // namespace oolong::codegen
