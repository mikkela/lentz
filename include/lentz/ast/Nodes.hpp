#pragma once

namespace lentz::ast {
    struct Node {
        virtual ~Node() = default;
    };

    struct Program final : Node {

    };
}