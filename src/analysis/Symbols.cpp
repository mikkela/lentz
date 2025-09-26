#include "lentz/analysis/Symbols.hpp"

namespace lentz::analysis {

    void SymbolTable::addClass(const std::string& name) {
        classes_.emplace(name, ClassInfo{name});
    }

    const ClassInfo* SymbolTable::lookupClass(const std::string& name) const {
        auto it = classes_.find(name);
        return it == classes_.end() ? nullptr : &it->second;
    }

    void predeclareClasses(SymbolTable& syms, const lentz::ast::Program& prog) {

    }

}
