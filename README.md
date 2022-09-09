# Kotlin compiler Backend Internal error
> Exception during IR lowering

This repository contains a reproducer what looks like a Kotlin compiler bug.

This problem was observed as part of https://github.com/gradle/gradle/pull/21910 which changes the Gradle Kotlin DSL to compiled against Kotlin language 1.7 and api 1.7. Previously, for backwards compatibility reasons, the Gradle Kotlin DSL was using 1.4.

This reproducer uses a snapshot of Gradle 7.6 that embedds Kotlin 1.7.10 and configures the `KotlinCompile` task to use Kotlin language 1.5 and api 1.5 which are the lowest that trigger the problem.

To reproduce run the following in this repository:
```shell
./gradlew run
```

The build will fail with:

```text
> Task :buildSrc:compileKotlin FAILED
e: org.jetbrains.kotlin.backend.common.BackendException: Backend Internal error: Exception during IR lowering
File being compiled: buildSrc/src/main/kotlin/my-plugin.gradle.kts
The root cause java.lang.RuntimeException was thrown at: org.jetbrains.kotlin.backend.jvm.codegen.FunctionCodegen.generate(FunctionCodegen.kt:50)

Caused by: java.lang.RuntimeException: Exception while generating code for:
FUN name:apply visibility:public modality:OPEN <> ($this:<root>.My_plugin_gradle.MyInlinePlugin, project:org.gradle.api.Project) returnType:kotlin.Unit
  overridden:
    public abstract fun apply (p0: @[EnhancedNullability] T of org.gradle.api.Plugin): kotlin.Unit declared in org.gradle.api.Plugin
  $this: VALUE_PARAMETER name:<this> type:<root>.My_plugin_gradle.MyInlinePlugin
  VALUE_PARAMETER name:project index:0 type:org.gradle.api.Project
  BLOCK_BODY
    COMPOSITE type=kotlin.Unit origin=null
      CALL 'public abstract fun register (p0: @[EnhancedNullability] kotlin.String, p1: @[EnhancedNullability] org.gradle.api.Action<in @[FlexibleNullability] org.gradle.api.Task?>): @[EnhancedNullability] org.gradle.api.tasks.TaskProvider<@[FlexibleNullability] org.gradle.api.Task?> declared in org.gradle.api.tasks.TaskContainer' type=@[EnhancedNullability] org.gradle.api.tasks.TaskProvider<@[FlexibleNullability] org.gradle.api.Task?> origin=null
        $this: CALL 'public abstract fun getTasks (): @[EnhancedNullability] org.gradle.api.tasks.TaskContainer declared in org.gradle.api.Project' type=@[EnhancedNullability] org.gradle.api.tasks.TaskContainer origin=GET_PROPERTY
          $this: CALL 'public final fun access$get$$implicitReceiver_Project$p ($this: <root>.My_plugin_gradle): org.gradle.api.Project declared in <root>.My_plugin_gradle' type=org.gradle.api.Project origin=null
            $this: GET_VAR '<this>: <root>.My_plugin_gradle declared in <root>.My_plugin_gradle' type=<root>.My_plugin_gradle origin=null
        p0: CONST String type=kotlin.String value="run"
        p1: BLOCK type=org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?> origin=LAMBDA
          COMPOSITE type=kotlin.Unit origin=null
          CALL 'public final fun <jvm-indy> <T> (dynamicCall: T of kotlin.jvm.internal.<jvm-indy>, bootstrapMethodHandle: kotlin.Any, vararg bootstrapMethodArguments: kotlin.Any): T of kotlin.jvm.internal.<jvm-indy> declared in kotlin.jvm.internal' type=org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?> origin=null
            <T>: org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?>
            dynamicCall: CALL 'public final fun execute (): org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?> declared in kotlin.jvm.internal.invokeDynamic' type=org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?> origin=null
            bootstrapMethodHandle: CALL 'public final fun <jvm-method-handle> (tag: kotlin.Int, owner: kotlin.String, name: kotlin.String, descriptor: kotlin.String, isInterface: kotlin.Boolean): kotlin.Any declared in kotlin.jvm.internal' type=kotlin.Any origin=null
              tag: CONST Int type=kotlin.Int value=6
              owner: CONST String type=kotlin.String value="java/lang/invoke/LambdaMetafactory"
              name: CONST String type=kotlin.String value="metafactory"
              descriptor: CONST String type=kotlin.String value="(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"
              isInterface: CONST Boolean type=kotlin.Boolean value=false
            bootstrapMethodArguments: VARARG type=kotlin.Array<kotlin.Any> varargElementType=kotlin.Any
              CALL 'public final fun <jvm-original-method-type> (method: kotlin.Any): kotlin.Any declared in kotlin.jvm.internal' type=kotlin.Any origin=null
                method: RAW_FUNCTION_REFERENCE 'public abstract fun execute (p0: @[EnhancedNullability] T of org.gradle.api.Action): kotlin.Unit declared in org.gradle.api.Action' type=kotlin.Any
              RAW_FUNCTION_REFERENCE 'private final fun apply$lambda-1 ($receiver: @[EnhancedNullability] org.gradle.api.Task): kotlin.Unit declared in <root>.My_plugin_gradle.MyInlinePlugin' type=kotlin.Any
              CALL 'public final fun <jvm-original-method-type> (method: kotlin.Any): kotlin.Any declared in kotlin.jvm.internal' type=kotlin.Any origin=null
                method: RAW_FUNCTION_REFERENCE 'public abstract fun execute (p0: @[FlexibleNullability] org.gradle.api.Task?): kotlin.Unit [fake_override] declared in kotlin.jvm.internal.invokeDynamic.<fake>' type=kotlin.Any
      COMPOSITE type=kotlin.Unit origin=null

Caused by: java.lang.IllegalStateException: No mapping for symbol: VALUE_PARAMETER INSTANCE_RECEIVER name:<this> type:<root>.My_plugin_gradle
```

<details>
<summary>Complete stacktrace</summary>

```text
> Task :buildSrc:compileKotlin FAILED
e: org.jetbrains.kotlin.backend.common.BackendException: Backend Internal error: Exception during IR lowering
File being compiled: buildSrc/src/main/kotlin/my-plugin.gradle.kts
The root cause java.lang.RuntimeException was thrown at: org.jetbrains.kotlin.backend.jvm.codegen.FunctionCodegen.generate(FunctionCodegen.kt:50)
        at org.jetbrains.kotlin.backend.common.CodegenUtil.reportBackendException(CodegenUtil.kt:239)
        at org.jetbrains.kotlin.backend.common.CodegenUtil.reportBackendException$default(CodegenUtil.kt:235)
        at org.jetbrains.kotlin.backend.common.phaser.PerformByIrFilePhase.invokeSequential(performByIrFile.kt:68)
        at org.jetbrains.kotlin.backend.common.phaser.PerformByIrFilePhase.invoke(performByIrFile.kt:55)
        at org.jetbrains.kotlin.backend.common.phaser.PerformByIrFilePhase.invoke(performByIrFile.kt:41)
        at org.jetbrains.kotlin.backend.common.phaser.NamedCompilerPhase.invoke(CompilerPhase.kt:96)
        at org.jetbrains.kotlin.backend.common.phaser.CompositePhase.invoke(PhaseBuilders.kt:29)
        at org.jetbrains.kotlin.backend.common.phaser.NamedCompilerPhase.invoke(CompilerPhase.kt:96)
        at org.jetbrains.kotlin.backend.common.phaser.CompilerPhaseKt.invokeToplevel(CompilerPhase.kt:43)
        at org.jetbrains.kotlin.backend.jvm.JvmIrCodegenFactory.invokeCodegen(JvmIrCodegenFactory.kt:284)
        at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.runCodegen(KotlinToJVMBytecodeCompiler.kt:355)
        at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.compileModules$cli(KotlinToJVMBytecodeCompiler.kt:136)
        at org.jetbrains.kotlin.cli.jvm.compiler.KotlinToJVMBytecodeCompiler.compileModules$cli$default(KotlinToJVMBytecodeCompiler.kt:60)
        at org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.kt:157)
        at org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.doExecute(K2JVMCompiler.kt:52)
        at org.jetbrains.kotlin.cli.common.CLICompiler.execImpl(CLICompiler.kt:94)
        at org.jetbrains.kotlin.cli.common.CLICompiler.execImpl(CLICompiler.kt:43)
        at org.jetbrains.kotlin.cli.common.CLITool.exec(CLITool.kt:101)
        at org.jetbrains.kotlin.incremental.IncrementalJvmCompilerRunner.runCompiler(IncrementalJvmCompilerRunner.kt:477)
        at org.jetbrains.kotlin.incremental.IncrementalJvmCompilerRunner.runCompiler(IncrementalJvmCompilerRunner.kt:127)
        at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.compileIncrementally(IncrementalCompilerRunner.kt:366)
        at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.compileIncrementally$default(IncrementalCompilerRunner.kt:311)
        at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.rebuild(IncrementalCompilerRunner.kt:110)
        at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.compileImpl(IncrementalCompilerRunner.kt:200)
        at org.jetbrains.kotlin.incremental.IncrementalCompilerRunner.compile(IncrementalCompilerRunner.kt:75)
        at org.jetbrains.kotlin.daemon.CompileServiceImplBase.execIncrementalCompiler(CompileServiceImpl.kt:625)
        at org.jetbrains.kotlin.daemon.CompileServiceImplBase.access$execIncrementalCompiler(CompileServiceImpl.kt:101)
        at org.jetbrains.kotlin.daemon.CompileServiceImpl.compile(CompileServiceImpl.kt:1739)
        at jdk.internal.reflect.GeneratedMethodAccessor101.invoke(Unknown Source)
        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
        at java.base/java.lang.reflect.Method.invoke(Method.java:566)
        at java.rmi/sun.rmi.server.UnicastServerRef.dispatch(UnicastServerRef.java:359)
        at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:200)
        at java.rmi/sun.rmi.transport.Transport$1.run(Transport.java:197)
        at java.base/java.security.AccessController.doPrivileged(Native Method)
        at java.rmi/sun.rmi.transport.Transport.serviceCall(Transport.java:196)
        at java.rmi/sun.rmi.transport.tcp.TCPTransport.handleMessages(TCPTransport.java:562)
        at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run0(TCPTransport.java:796)
        at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.lambda$run$0(TCPTransport.java:677)
        at java.base/java.security.AccessController.doPrivileged(Native Method)
        at java.rmi/sun.rmi.transport.tcp.TCPTransport$ConnectionHandler.run(TCPTransport.java:676)
        at java.base/java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1128)
        at java.base/java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:628)
        at java.base/java.lang.Thread.run(Thread.java:829)
Caused by: java.lang.RuntimeException: Exception while generating code for:
FUN name:apply visibility:public modality:OPEN <> ($this:<root>.My_plugin_gradle.MyInlinePlugin, project:org.gradle.api.Project) returnType:kotlin.Unit
  overridden:
    public abstract fun apply (p0: @[EnhancedNullability] T of org.gradle.api.Plugin): kotlin.Unit declared in org.gradle.api.Plugin
  $this: VALUE_PARAMETER name:<this> type:<root>.My_plugin_gradle.MyInlinePlugin
  VALUE_PARAMETER name:project index:0 type:org.gradle.api.Project
  BLOCK_BODY
    COMPOSITE type=kotlin.Unit origin=null
      CALL 'public abstract fun register (p0: @[EnhancedNullability] kotlin.String, p1: @[EnhancedNullability] org.gradle.api.Action<in @[FlexibleNullability] org.gradle.api.Task?>): @[EnhancedNullability] org.gradle.api.tasks.TaskProvider<@[FlexibleNullability] org.gradle.api.Task?> declared in org.gradle.api.tasks.TaskContainer' type=@[EnhancedNullability] org.gradle.api.tasks.TaskProvider<@[FlexibleNullability] org.gradle.api.Task?> origin=null
        $this: CALL 'public abstract fun getTasks (): @[EnhancedNullability] org.gradle.api.tasks.TaskContainer declared in org.gradle.api.Project' type=@[EnhancedNullability] org.gradle.api.tasks.TaskContainer origin=GET_PROPERTY
          $this: CALL 'public final fun access$get$$implicitReceiver_Project$p ($this: <root>.My_plugin_gradle): org.gradle.api.Project declared in <root>.My_plugin_gradle' type=org.gradle.api.Project origin=null
            $this: GET_VAR '<this>: <root>.My_plugin_gradle declared in <root>.My_plugin_gradle' type=<root>.My_plugin_gradle origin=null
        p0: CONST String type=kotlin.String value="run"
        p1: BLOCK type=org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?> origin=LAMBDA
          COMPOSITE type=kotlin.Unit origin=null
          CALL 'public final fun <jvm-indy> <T> (dynamicCall: T of kotlin.jvm.internal.<jvm-indy>, bootstrapMethodHandle: kotlin.Any, vararg bootstrapMethodArguments: kotlin.Any): T of kotlin.jvm.internal.<jvm-indy> declared in kotlin.jvm.internal' type=org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?> origin=null
            <T>: org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?>
            dynamicCall: CALL 'public final fun execute (): org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?> declared in kotlin.jvm.internal.invokeDynamic' type=org.gradle.api.Action<@[FlexibleNullability] org.gradle.api.Task?> origin=null
            bootstrapMethodHandle: CALL 'public final fun <jvm-method-handle> (tag: kotlin.Int, owner: kotlin.String, name: kotlin.String, descriptor: kotlin.String, isInterface: kotlin.Boolean): kotlin.Any declared in kotlin.jvm.internal' type=kotlin.Any origin=null
              tag: CONST Int type=kotlin.Int value=6
              owner: CONST String type=kotlin.String value="java/lang/invoke/LambdaMetafactory"
              name: CONST String type=kotlin.String value="metafactory"
              descriptor: CONST String type=kotlin.String value="(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;"
              isInterface: CONST Boolean type=kotlin.Boolean value=false
            bootstrapMethodArguments: VARARG type=kotlin.Array<kotlin.Any> varargElementType=kotlin.Any
              CALL 'public final fun <jvm-original-method-type> (method: kotlin.Any): kotlin.Any declared in kotlin.jvm.internal' type=kotlin.Any origin=null
                method: RAW_FUNCTION_REFERENCE 'public abstract fun execute (p0: @[EnhancedNullability] T of org.gradle.api.Action): kotlin.Unit declared in org.gradle.api.Action' type=kotlin.Any
              RAW_FUNCTION_REFERENCE 'private final fun apply$lambda-1 ($receiver: @[EnhancedNullability] org.gradle.api.Task): kotlin.Unit declared in <root>.My_plugin_gradle.MyInlinePlugin' type=kotlin.Any
              CALL 'public final fun <jvm-original-method-type> (method: kotlin.Any): kotlin.Any declared in kotlin.jvm.internal' type=kotlin.Any origin=null
                method: RAW_FUNCTION_REFERENCE 'public abstract fun execute (p0: @[FlexibleNullability] org.gradle.api.Task?): kotlin.Unit [fake_override] declared in kotlin.jvm.internal.invokeDynamic.<fake>' type=kotlin.Any
      COMPOSITE type=kotlin.Unit origin=null

        at org.jetbrains.kotlin.backend.jvm.codegen.FunctionCodegen.generate(FunctionCodegen.kt:50)
        at org.jetbrains.kotlin.backend.jvm.codegen.FunctionCodegen.generate$default(FunctionCodegen.kt:43)
        at org.jetbrains.kotlin.backend.jvm.codegen.ClassCodegen.generateMethodNode(ClassCodegen.kt:380)
        at org.jetbrains.kotlin.backend.jvm.codegen.ClassCodegen.generateMethod(ClassCodegen.kt:397)
        at org.jetbrains.kotlin.backend.jvm.codegen.ClassCodegen.generate(ClassCodegen.kt:148)
        at org.jetbrains.kotlin.backend.jvm.codegen.ClassCodegen.generate(ClassCodegen.kt:161)
        at org.jetbrains.kotlin.backend.jvm.FileCodegen.lower(JvmPhases.kt:44)
        at org.jetbrains.kotlin.backend.common.phaser.FileLoweringPhaseAdapter.invoke(PhaseBuilders.kt:120)
        at org.jetbrains.kotlin.backend.common.phaser.FileLoweringPhaseAdapter.invoke(PhaseBuilders.kt:116)
        at org.jetbrains.kotlin.backend.common.phaser.NamedCompilerPhase.invoke(CompilerPhase.kt:96)
        at org.jetbrains.kotlin.backend.common.phaser.PerformByIrFilePhase.invokeSequential(performByIrFile.kt:65)
        ... 41 more
Caused by: java.lang.IllegalStateException: No mapping for symbol: VALUE_PARAMETER INSTANCE_RECEIVER name:<this> type:<root>.My_plugin_gradle
        at org.jetbrains.kotlin.backend.jvm.codegen.IrFrameMap.typeOf(irCodegenUtils.kt:62)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitGetValue(ExpressionCodegen.kt:680)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitGetValue(ExpressionCodegen.kt:130)
        at org.jetbrains.kotlin.ir.expressions.IrGetValue.accept(IrGetValue.kt:12)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.gen(ExpressionCodegen.kt:211)
        at org.jetbrains.kotlin.backend.jvm.codegen.IrCallGenerator$DefaultImpls.genValueAndPut(IrCallGenerator.kt:50)
        at org.jetbrains.kotlin.backend.jvm.codegen.IrCallGenerator$DefaultCallGenerator.genValueAndPut(IrCallGenerator.kt:53)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitCall$handleValueParameter(ExpressionCodegen.kt:500)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitCall(ExpressionCodegen.kt:514)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitCall(ExpressionCodegen.kt:130)
        at org.jetbrains.kotlin.ir.expressions.IrCall.accept(IrCall.kt:17)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.gen(ExpressionCodegen.kt:211)
        at org.jetbrains.kotlin.backend.jvm.codegen.IrCallGenerator$DefaultImpls.genValueAndPut(IrCallGenerator.kt:50)
        at org.jetbrains.kotlin.backend.jvm.codegen.IrCallGenerator$DefaultCallGenerator.genValueAndPut(IrCallGenerator.kt:53)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitCall(ExpressionCodegen.kt:491)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitCall(ExpressionCodegen.kt:130)
        at org.jetbrains.kotlin.ir.expressions.IrCall.accept(IrCall.kt:17)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.gen(ExpressionCodegen.kt:211)
        at org.jetbrains.kotlin.backend.jvm.codegen.IrCallGenerator$DefaultImpls.genValueAndPut(IrCallGenerator.kt:50)
        at org.jetbrains.kotlin.backend.jvm.codegen.IrCallGenerator$DefaultCallGenerator.genValueAndPut(IrCallGenerator.kt:53)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitCall(ExpressionCodegen.kt:491)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitCall(ExpressionCodegen.kt:130)
        at org.jetbrains.kotlin.ir.expressions.IrCall.accept(IrCall.kt:17)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitStatementContainer(ExpressionCodegen.kt:457)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitContainerExpression(ExpressionCodegen.kt:470)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitContainerExpression(ExpressionCodegen.kt:130)
        at org.jetbrains.kotlin.ir.visitors.IrElementVisitor$DefaultImpls.visitComposite(IrElementVisitor.kt:65)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitComposite(ExpressionCodegen.kt:130)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitComposite(ExpressionCodegen.kt:130)
        at org.jetbrains.kotlin.ir.expressions.IrComposite.accept(IrComposite.kt:15)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitStatementContainer(ExpressionCodegen.kt:457)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitBlockBody(ExpressionCodegen.kt:461)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.visitBlockBody(ExpressionCodegen.kt:130)
        at org.jetbrains.kotlin.ir.expressions.IrBlockBody.accept(IrBlockBody.kt:17)
        at org.jetbrains.kotlin.backend.jvm.codegen.ExpressionCodegen.generate(ExpressionCodegen.kt:234)
        at org.jetbrains.kotlin.backend.jvm.codegen.FunctionCodegen.doGenerate(FunctionCodegen.kt:122)
        at org.jetbrains.kotlin.backend.jvm.codegen.FunctionCodegen.generate(FunctionCodegen.kt:48)
        ... 51 more
```

</details>

You can change the `buildSrc/src/main/kotlin/my-plugin.gradle.kts` file to tweak the compiler configuration.

Note that using `-Xuse-old-backend` makes the problem go away. This is only applicable with Kotlin language and api 1.5 though.


