package de.unistuttgart.ipvs.pmp.model.interfaces;

import de.unistuttgart.ipvs.pmp.PMPComponentType;

/**
 * The {@link IModel} provides all {@link IApp}s, {@link IPreset}s and
 * {@link IResourceGroup}s known by PMP.
 * 
 * @author Jakob Jarosch
 */
public interface IModel {

    /**
     * @return Returns all {@link IApp}s known by PMP.
     */
    public IApp[] getApps();



    public IApp getApp(String identifier);
    
    /**
     * Register a new App at PMP.
     * 
     * @param identifier
     * @param publicKey
     */
    public void addApp(String identifier, byte[] publicKey);

    /**
     * @return Returns all {@link IResourceGroup}s known by PMP.
     */
    public IResourceGroup[] getResourceGroups();
    
    public IApp getResourceGroup(String identifier);
    
    /**
     * Register a new ResourceGroup at PMP.
     * 
     * @param identifier
     * @param publicKey
     */
    public void addResourceGroup(String identifier, byte[] publicKey);

    /**
     * @return Returns all {@link IPreset}s known by PMP.
     */
    public IPreset[] getPresets();
    
    public IPreset addPreset(String name, String description, PMPComponentType type, String identifier);


}