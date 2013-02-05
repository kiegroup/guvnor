package org.kie.guvnor.datamodel.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kie.guvnor.datamodel.model.MethodInfo;
import org.kie.guvnor.datamodel.model.ModelField;
import org.kie.guvnor.services.config.model.imports.Import;
import org.kie.guvnor.services.config.model.imports.Imports;

/**
 * Utilities for PackageDataModelOracle
 */
public class PackageDataModelOracleUtils {

    //Filter and rename Model Fields based on package name and imports
    public static Map<String, ModelField[]> filterModelFields( final String packageName,
                                                               final Imports imports,
                                                               final Map<String, ModelField[]> projectModelFields ) {
        final Map<String, ModelField[]> scopedModelFields = new HashMap<String, ModelField[]>();
        for ( Map.Entry<String, ModelField[]> e : projectModelFields.entrySet() ) {
            final String mfQualifiedType = e.getKey();
            final String mfPackageName = getPackageName( mfQualifiedType );
            final String mfTypeName = getTypeName( mfQualifiedType );

            if ( mfPackageName.equals( packageName ) || isImported( mfQualifiedType,
                                                                    imports ) ) {
                scopedModelFields.put( mfTypeName,
                                       correctModelFields( packageName,
                                                           e.getValue(),
                                                           imports ) );
            }
        }
        return scopedModelFields;
    }

    //Filter and rename Event Types based on package name and imports
    public static Map<String, Boolean> filterEventTypes( final String packageName,
                                                         final Imports imports,
                                                         final Map<String, Boolean> projectEventTypes ) {
        final Map<String, Boolean> scopedEventTypes = new HashMap<String, Boolean>();
        for ( Map.Entry<String, Boolean> e : projectEventTypes.entrySet() ) {
            final String eventQualifiedType = e.getKey();
            final String eventPackageName = getPackageName( eventQualifiedType );
            final String eventTypeName = getTypeName( eventQualifiedType );

            if ( eventPackageName.equals( packageName ) || isImported( eventQualifiedType,
                                                                       imports ) ) {
                scopedEventTypes.put( eventTypeName,
                                      e.getValue() );
            }
        }
        return scopedEventTypes;
    }

    //Filter and rename Enum definitions based on package name and imports
    public static Map<String, String[]> filterEnumDefinitions( final String packageName,
                                                               final Imports imports,
                                                               final Map<String, String[]> enumDefinitions ) {
        final Map<String, String[]> scopedEnumLists = new HashMap<String, String[]>();
        for ( Map.Entry<String, String[]> e : enumDefinitions.entrySet() ) {
            final String enumQualifiedType = e.getKey();
            final String enumPackageName = getPackageName( enumQualifiedType );
            final String enumTypeName = getTypeName( enumQualifiedType );

            if ( enumPackageName.equals( packageName ) || isImported( enumQualifiedType,
                                                                      imports ) ) {
                scopedEnumLists.put( enumTypeName,
                                     e.getValue() );
            }
        }
        return scopedEnumLists;
    }

    //TODO Filter and rename based on package name (and imports)
    public static Map<String, List<MethodInfo>> filterMethodInformation( final String packageName,
                                                                         final List<String> imports,
                                                                         final Map<String, List<MethodInfo>> projectMethodInformation ) {
        final Map<String, List<MethodInfo>> scopedMethodInformation = new HashMap<String, List<MethodInfo>>();
        return scopedMethodInformation;
    }

    //TODO Filter and rename based on package name (and imports)
    public static Map<String, String> filterFieldParametersTypes( final String packageName,
                                                                  final List<String> imports,
                                                                  final Map<String, String> projectFieldParametersTypes ) {
        final Map<String, String> scopedFieldParametersType = new HashMap<String, String>();
        return scopedFieldParametersType;
    }

    private static String getPackageName( final String qualifiedType ) {
        String packageName = qualifiedType;
        int dotIndex = packageName.lastIndexOf( "." );
        if ( dotIndex != -1 ) {
            return packageName.substring( 0,
                                          dotIndex );
        }
        return "";
    }

    private static String getTypeName( final String qualifiedType ) {
        String typeName = qualifiedType;
        int dotIndex = typeName.lastIndexOf( "." );
        if ( dotIndex != -1 ) {
            typeName = typeName.substring( dotIndex + 1 );
        }
        return typeName.replace( "$",
                                 "." );
    }

    private static ModelField[] correctModelFields( final String packageName,
                                                    final ModelField[] originalModelFields,
                                                    final Imports imports ) {
        final List<ModelField> correctedModelFields = new ArrayList<ModelField>();
        for ( final ModelField mf : originalModelFields ) {
            String mfType = mf.getType();
            String mfClassName = mf.getClassName();
            final String mfClassName_QualifiedType = mf.getClassName();
            final String mfClassName_PackageName = getPackageName( mfClassName_QualifiedType );
            final String mfClassName_TypeName = getTypeName( mfClassName_QualifiedType );

            if ( mfClassName_PackageName.equals( packageName ) || isImported( mfClassName_QualifiedType,
                                                                              imports ) ) {
                mfClassName = mfClassName_TypeName;
            }

            final String mfType_QualifiedType = mf.getType();
            final String mfType_PackageName = getPackageName( mfType_QualifiedType );
            final String mfType_TypeName = getTypeName( mfType_QualifiedType );
            if ( mfType_PackageName.equals( packageName ) || isImported( mfType_QualifiedType,
                                                                         imports ) ) {
                mfType = mfType_TypeName;
            }
            correctedModelFields.add( new ModelField( mf.getName(),
                                                      mfClassName,
                                                      mf.getClassType(),
                                                      mf.getAccessorsAndMutators(),
                                                      mfType ) );
        }
        final ModelField[] result = new ModelField[ correctedModelFields.size() ];
        return correctedModelFields.toArray( result );
    }

    private static boolean isImported( final String qualifiedType,
                                       final Imports imports ) {
        final Import item = new Import( qualifiedType );
        return imports.contains( item );
    }

}
