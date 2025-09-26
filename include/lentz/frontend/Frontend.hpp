#pragma once
#include <memory>
#include <string>
#include "ast/Nodes.hpp"

namespace lentz::frontend {
    struct Source {
        std::string path;
        std::string text;
    };

    class Frontend {
    public:
        std::unique_ptr<ast::Program> parse(const Source& src);
    };
} // namespace oolong::frontend
