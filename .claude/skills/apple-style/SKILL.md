---
name: apple-style
version: 1.1.0
description: "Use when: 在 Jetpack Compose 中应用 Apple 风格 UI 设计。"
keywords: [Apple, 极简, 高级, 玻璃态, Bento, Compose]
author: StyleKit
style_type: visual
---

# Apple 风格（Compose 适配版）

## 概述
面向 Jetpack Compose 的 Apple 风格设计规范，强调极简、克制、精致与清晰的层级。重点体现：Bento 网格、柔和玻璃质感、单一强调色与高可读性排版。

## 适用场景
- 需要高端、简约、可信赖的产品气质
- 需要强可读性与强秩序的卡片化信息展示
- 需要统一交互层级与克制动效

## 技术栈适配原则
- 使用 Jetpack Compose + Material3
- 颜色、排版、形状统一通过 `MaterialTheme` 与 `Typography`
- 背景与卡片通过 `Surface`、`Card` 与 `Box` 组合实现
- 动效使用 `animate*AsState` 与 `AnimatedVisibility`

---

# 1. 布局与结构：Bento Grid
**目标：** 内容模块化、卡片化、留白充足。

- 间距：24dp - 32dp；全局使用 `Arrangement.spacedBy(24.dp)`
- 大屏限制最大宽度：`widthIn(max = 1120.dp)` 并居中
- 小屏自动堆叠：使用 `GridCells.Adaptive(minSize = 280.dp)`

**Compose 推荐：**
```kotlin
BoxWithConstraints(Modifier.fillMaxSize()) {
    val maxWidth = 1120.dp
    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 280.dp),
            modifier = Modifier
                .widthIn(max = maxWidth)
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) { /* cards */ }
    }
}
```

---

# 2. 氛围与材质：Aurora + Glass
**目标：** 低饱和极光背景 + 半透明玻璃卡片。

## 2.1 Aurora 背景
- Base：`#F5F5F7`
- Orbs：2-3 个大圆（50vw-60vw）
- Gradient：淡蓝 `rgba(162,210,255,0.4)` + 淡紫 `rgba(200,180,255,0.3)`
- Blur：`blur(80px)` 或更高
- Animation：缓慢浮动

**Compose 思路：**
- 使用 `Box` 或 `Canvas` 绘制 `Brush.radialGradient`
- API 31+ 使用 `RenderEffect` 进行高斯模糊
- 低版本保持低透明度 + 大尺寸渐变模拟

```kotlin
val blur = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    RenderEffect.createBlurEffect(80f, 80f, Shader.TileMode.CLAMP)
} else null

Box(
    Modifier
        .fillMaxSize()
        .background(Color(0xFFF5F5F7))
) {
    Box(
        Modifier
            .size(520.dp)
            .offset(x = (-120).dp, y = (-80).dp)
            .graphicsLayer { renderEffect = blur }
            .background(
                Brush.radialGradient(
                    listOf(Color(0x66A2D2FF), Color.Transparent)
                ),
                shape = CircleShape
            )
    )
    Box(
        Modifier
            .size(520.dp)
            .offset(x = 160.dp, y = 220.dp)
            .graphicsLayer { renderEffect = blur }
            .background(
                Brush.radialGradient(
                    listOf(Color(0x4CC8B4FF), Color.Transparent)
                ),
                shape = CircleShape
            )
    )
}
```

## 2.2 Glass 卡片
- 背景：`Color.White.copy(alpha = 0.8f)`
- 边框：1dp 低对比线 `Color(0x1A000000)`
- 圆角：24dp（连续曲率）
- 阴影：轻微、低透明度

---

# 3. 组件层级：按钮与输入框
**主按钮：** 品牌蓝实心、胶囊圆角
- Color：`#0071E3`
- 文字：白色
- Shape：`RoundedCornerShape(999.dp)`

**次按钮：** 浅灰实心（禁止透明）
- Color：`#E5E5EA`
- 文字：深色

**输入框：** 无边框、浅灰底、聚焦时白底+蓝色光晕

**Compose 推荐：**
```kotlin
Button(
    onClick = {},
    shape = RoundedCornerShape(999.dp),
    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0071E3))
) { Text("继续", color = Color.White) }

Button(
    onClick = {},
    shape = RoundedCornerShape(999.dp),
    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E5EA))
) { Text("取消", color = Color(0xFF1D1D1F)) }

TextField(
    value = text,
    onValueChange = { text = it },
    colors = TextFieldDefaults.colors(
        focusedContainerColor = Color.White,
        unfocusedContainerColor = Color(0xFFF2F2F7),
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent
    )
)
```

---

# 4. 排版与信息：Big Type
**字体：** 使用项目内字体资源（PingFang），由 `Type.kt` 统一配置。

**层级建议：**
- Display / Headline：`FontWeight.Medium`
- Body：`FontWeight.Normal`
- Caption：`Color(0xFF86868B)`
- 大标题字距微缩：`letterSpacing = (-0.2).sp` 或 `0.sp`，避免过紧

---

# 5. 动效：Physics
**缓动：** `CubicBezierEasing(0.25f, 1f, 0.5f, 1f)`
**反馈：** 按压时 `scale(0.98f)`，悬浮/聚焦时 `scale(1.02f)`
**入场：** `fade-in-up` 序列动画（从 8dp 向上）

**Compose 推荐：**
```kotlin
val easing = CubicBezierEasing(0.25f, 1f, 0.5f, 1f)
val scale by animateFloatAsState(
    targetValue = if (pressed) 0.98f else 1f,
    animationSpec = tween(180, easing = easing)
)

Card(
    modifier = Modifier
        .graphicsLayer { scaleX = scale; scaleY = scale }
)
```

---

# 6. 细节修饰
- Hairline：1dp 半透明线 `Color(0x1A000000)`
- 图标：使用 Material Icons 的 Outline 风格，尺寸 20dp-24dp
- 色彩：95% 灰度系统，唯一强调色为 Apple 蓝

---

## 禁止事项
- 组件使用强渐变、重阴影、粗边框
- 多强调色并存
- 过于复杂的装饰与纹理
- 重字重堆叠（仅少量 Medium/Bold）

---

## 使用提示
当需要“极简、高级、Apple 质感”的界面时，优先应用本技能，并在 Compose 中统一通过主题、形状与动效规则实现。
