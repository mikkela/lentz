#pragma once
#include <string>
#include <unordered_map>
#include <vector>
#include "lentz/ast/Nodes.hpp"

namespace lentz::analysis {

    struct ClassInfo {
        std::string name;
    };

    class SymbolTable {
    public:
        void addClass(const std::string& name);
        const ClassInfo* lookupClass(const std::string& name) const;
    private:
        std::unordered_map<std::string, ClassInfo> classes_;
    };

    void predeclareClasses(SymbolTable& syms, const lentz::ast::Program& prog);

} // namespace oolong::sem
