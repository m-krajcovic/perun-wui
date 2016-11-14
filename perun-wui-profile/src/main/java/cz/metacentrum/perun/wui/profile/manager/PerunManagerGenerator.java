package cz.metacentrum.perun.wui.profile.manager;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import com.google.gwt.core.ext.typeinfo.JParameter;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @author Michal Krajcovic <mkrajcovic@mail.muni.cz>
 */
public class PerunManagerGenerator extends Generator {

	@Override
	public String generate(TreeLogger logger, GeneratorContext ctx,
						   String requestedClass) throws UnableToCompleteException {

		TypeOracle typeOracle = ctx.getTypeOracle();
		assert (typeOracle != null);

		JClassType classType = typeOracle.findType(requestedClass);
		if (classType == null) {
			logger.log(TreeLogger.ERROR, "Unable to find metadata for type '"
					+ requestedClass + "'", null);
			throw new UnableToCompleteException();
		}

		if (classType.isInterface() == null) {
			logger.log(TreeLogger.ERROR, classType.getQualifiedSourceName()
					+ " is not an interface", null);
			throw new UnableToCompleteException();
		}

		String packageName = classType.getPackage().getName();
		String implName = classType.getSimpleSourceName() + "Impl";
		ClassSourceFileComposerFactory composerFactory =
				new ClassSourceFileComposerFactory(packageName, implName);

		composerFactory.addImport(classType.getQualifiedSourceName());
		composerFactory.addImport("cz.metacentrum.perun.wui.json.JsonClient");
		composerFactory.addImplementedInterface(classType.getQualifiedSourceName());

		PrintWriter printWriter = ctx
				.tryCreate(logger, packageName, implName);
		SourceWriter sourceWriter = composerFactory
				.createSourceWriter(ctx, printWriter);

		String managerName = getManagerName(classType);

		for (JMethod jMethod : classType.getMethods()) {
			generateMethod(sourceWriter, managerName, jMethod);
		}

		sourceWriter.commit(logger);
		return packageName + "." + implName;
	}

	private String getManagerName(JClassType classType) {
		String managerName = classType.getSimpleSourceName();
		managerName = managerName.substring(0, 1).toLowerCase() + managerName.substring(1);
		if (classType.isAnnotationPresent(PerunManager.class)) {
			PerunManager perunManager = classType.getAnnotation(PerunManager.class);
			if (!perunManager.value().isEmpty()) {
				managerName = perunManager.value();
			}
		}
		return managerName;
	}

	private void generateMethod(SourceWriter sourceWriter, String managerName, JMethod jMethod) {
		sourceWriter.print("\n");
		sourceWriter.print("  " + jMethod.getReadableDeclaration(false, false, false, false, true) + " {\n");
		if (isMethodSupported(jMethod)) {
			generateMethodBody(sourceWriter, managerName, jMethod);
		} else {
			sourceWriter.print("    throw new UnsupportedOperationException();\n");
		}

		sourceWriter.print("  }\n");
	}

	private void generateMethodBody(SourceWriter sourceWriter, String managerName, JMethod jMethod) {
		List<JParameter> parameters = new ArrayList<>(Arrays.asList(jMethod.getParameters()));
		String eventsParameterName = findJsonEventsParameterAndRemoveIt(parameters);
		sourceWriter.print("    JsonClient client = new JsonClient(" + eventsParameterName + ");\n");
		for (JParameter parameter : parameters) {
			generateParametersInput(sourceWriter, parameter);
		}

		String methodName = getMethodName(jMethod);

		sourceWriter.print("    return client.call(\"" + managerName + "/" + methodName + "\");\n");
	}

	private void generateParametersInput(SourceWriter sourceWriter, JParameter parameter) {
		String parameterName = parameter.getName();
		if (parameter.isAnnotationPresent(PerunParam.class)) {
			PerunParam perunParam = parameter.getAnnotation(PerunParam.class);
			if (!perunParam.value().isEmpty()) {
				parameterName = perunParam.value();
			}
			if (parameter.getType().isPrimitive() == null && perunParam.nonNull()) {
				sourceWriter.print("    if (" + parameter.getName() + " == null) return null;\n");
			}
		} else {
			if (parameter.getType().isPrimitive() == null) {
				sourceWriter.print("    if (" + parameter.getName() + " == null) return null;\n");
			}
		}
		sourceWriter.print("    client.put(\"" + parameterName + "\", " + parameter.getName() + ");\n");
	}

	private String findJsonEventsParameterAndRemoveIt(List<JParameter> parameters) {
		String eventsParameter = "events";
		Iterator<JParameter> iterator = parameters.iterator();
		while (iterator.hasNext()) {
			JParameter parameter = iterator.next();
			if (parameter.getType().getQualifiedSourceName().equals("cz.metacentrum.perun.wui.json.JsonEvents")) {
				eventsParameter = parameter.getName();
				iterator.remove();
				break;
			}
		}
		return eventsParameter;
	}

	private String getMethodName(JMethod jMethod) {
		String methodName = jMethod.getName();
		if (jMethod.isAnnotationPresent(PerunCall.class)) {
			PerunCall perunCall = jMethod.getAnnotation(PerunCall.class);
			if (!perunCall.value().isEmpty()) {
				methodName = perunCall.value();
			}
		}
		return methodName;
	}

	private boolean isMethodSupported(JMethod jMethod) {
		return jMethod.getReturnType().getQualifiedSourceName().equals("com.google.gwt.http.client.Request");
	}
}
